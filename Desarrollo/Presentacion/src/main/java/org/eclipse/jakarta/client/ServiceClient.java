package org.eclipse.jakarta.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.*;
import org.eclipse.jakarta.dto.*;

import java.io.Serializable;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ServiceClient implements Serializable {

    private static final String CONTENT = env("CONTENT_SERVICE_URL",        "http://content-service:8080");
    private static final String USER    = env("USER_SERVICE_URL",            "http://user-service:8080");
    private static final String RECOMM  = env("RECOMMENDATION_SERVICE_URL", "http://recommendation-service:8080");
    private static final String SIMUL   = env("SIMULATION_SERVICE_URL",     "http://simulation-service:8080");

    private transient HttpClient http;

    private HttpClient http() {
        if (http == null) http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10)).build();
        return http;
    }

    // ── Content: course structure ─────────────────────────────────────────────

    public List<ModuleItem> getModules(long courseId) {
        return array(get(CONTENT + "/api/courses/" + courseId + "/modules")).stream()
                .map(o -> new ModuleItem(num(o, "id"), o.getString("title", ""))).toList();
    }

    public List<LessonItem> getLessons(long moduleId) {
        return array(get(CONTENT + "/api/modules/" + moduleId + "/lessons")).stream()
                .map(o -> new LessonItem(num(o, "id"), o.getString("title", ""),
                        o.getInt("position", 0))).toList();
    }

    public List<ContentItem> getContents(long lessonId) {
        return array(get(CONTENT + "/api/lessons/" + lessonId + "/contents")).stream()
                .map(o -> new ContentItem(num(o, "id"), o.getString("type", ""),
                        o.getInt("position", 0))).toList();
    }

    public List<ContentItem> getReinforcementContents(long lessonId) {
        return array(get(CONTENT + "/api/lessons/" + lessonId + "/reinforcement")).stream()
                .map(o -> new ContentItem(num(o, "id"), o.getString("type", ""),
                        o.getInt("position", 0))).toList();
    }

    // ── Content: media detail ─────────────────────────────────────────────────

    public VideoItem getVideo(long contentId) {
        String json = get(CONTENT + "/api/contents/" + contentId + "/video");
        if (json == null) return null;
        try {
            JsonObject o = obj(json);
            return new VideoItem(num(o, "id"), o.getString("url", ""),
                    o.containsKey("duration") ? o.getInt("duration", 0) : 0,
                    o.getString("provider", ""));
        } catch (Exception e) { return null; }
    }

    public ArticleItem getArticle(long contentId) {
        String json = get(CONTENT + "/api/contents/" + contentId + "/article");
        if (json == null) return null;
        try {
            JsonObject o = obj(json);
            return new ArticleItem(num(o, "id"), o.getString("body", ""));
        } catch (Exception e) { return null; }
    }

    public QuizItem getQuizContent(long contentId) {
        String json = get(CONTENT + "/api/contents/" + contentId + "/quiz");
        if (json == null) return null;
        try {
            JsonObject o = obj(json);
            List<QuestionAnswerItem> questions = new ArrayList<>();
            JsonArray qs = o.containsKey("questions") ? o.getJsonArray("questions")
                    : Json.createArrayBuilder().build();
            for (JsonValue qv : qs) {
                JsonObject q = qv.asJsonObject();
                List<AnswerOption> answers = new ArrayList<>();
                JsonArray as = q.containsKey("answers") ? q.getJsonArray("answers")
                        : Json.createArrayBuilder().build();
                for (JsonValue av : as) {
                    JsonObject a = av.asJsonObject();
                    answers.add(new AnswerOption(num(a, "id"), a.getString("answerText", "")));
                }
                questions.add(new QuestionAnswerItem(num(q, "id"),
                        q.getString("questionText", ""), answers));
            }
            return new QuizItem(num(o, "id"), num(o, "contentId"),
                    o.getString("title", "Quiz"), questions);
        } catch (Exception e) { return null; }
    }

    public long getCorrectAnswerId(long questionId) {
        String json = get(CONTENT + "/api/questions/" + questionId + "/correct-answer-id");
        if (json == null) return -1L;
        try { return num(obj(json), "answerId"); } catch (Exception e) { return -1L; }
    }

    // ── User service ──────────────────────────────────────────────────────────

    public EnrollmentItem getEnrollment(long enrollmentId) {
        String json = get(USER + "/api/enrollments/" + enrollmentId);
        if (json == null) return null;
        try {
            JsonObject o = obj(json);
            return new EnrollmentItem(num(o, "id"), num(o, "userId"), num(o, "courseId"),
                    o.containsKey("progress") ? o.getJsonNumber("progress").doubleValue() : 0.0);
        } catch (Exception e) { return null; }
    }

    public List<ProgressItem> getProgress(long userId) {
        return array(get(USER + "/api/lesson-progress/user/" + userId)).stream()
                .map(o -> new ProgressItem(num(o, "id"), num(o, "lessonId"),
                        o.getString("status", ""),
                        o.containsKey("timeSpent") ? o.getInt("timeSpent", 0) : 0)).toList();
    }

    public List<QuizAttemptItem> getQuizAttempts(long userId) {
        return array(get(USER + "/api/quiz-attempts/user/" + userId)).stream()
                .map(o -> new QuizAttemptItem(num(o, "id"), num(o, "quizId"),
                        o.containsKey("score")    ? o.getJsonNumber("score").doubleValue()    : 0.0,
                        o.containsKey("maxScore") ? o.getJsonNumber("maxScore").doubleValue() : 0.0)).toList();
    }

    public long startLesson(long userId, long lessonId) {
        String body = Json.createObjectBuilder()
                .add("userId", userId).add("lessonId", lessonId).build().toString();
        String resp = post(USER + "/api/lesson-progress/start", body);
        if (resp == null) return -1L;
        try { return num(obj(resp), "id"); } catch (Exception e) { return -1L; }
    }

    public void completeLesson(long progressId) {
        String body = Json.createObjectBuilder().add("timeSpent", 0).build().toString();
        put(USER + "/api/lesson-progress/" + progressId + "/complete", body);
    }

    public String submitQuiz(long userId, long quizId, Map<Long, Long> answers) {
        JsonArrayBuilder attArr = Json.createArrayBuilder();
        for (Map.Entry<Long, Long> e : answers.entrySet()) {
            long correctId = getCorrectAnswerId(e.getKey());
            attArr.add(Json.createObjectBuilder()
                    .add("questionId", e.getKey())
                    .add("answerId",   e.getValue())
                    .add("correct",    e.getValue().equals(correctId))
                    .add("timeSpent",  30));
        }
        String body = Json.createObjectBuilder()
                .add("userId", userId).add("quizId", quizId)
                .add("questionAttempts", attArr).build().toString();
        return post(USER + "/api/quiz-attempts", body);
    }

    // ── Simulation service ────────────────────────────────────────────────────

    public String runSimulation(long enrollmentId) {
        return post(SIMUL + "/api/simulations/enrollments/" + enrollmentId, null);
    }

    // ── Recommendation service ────────────────────────────────────────────────

    public List<RecommendationItem> getRecommendations(long userId) {
        return array(get(RECOMM + "/api/recommendations/user/" + userId)).stream()
                .map(o -> new RecommendationItem(num(o, "id"), num(o, "lessonId"),
                        o.containsKey("score") ? o.getJsonNumber("score").doubleValue() : 0.0,
                        o.getString("reason", ""))).toList();
    }

    public String runAnalysis(long enrollmentId) {
        return post(RECOMM + "/api/analysis/enrollments/" + enrollmentId, null);
    }

    public String runLessonAnalysis(long enrollmentId, long lessonId) {
        return post(RECOMM + "/api/analysis/enrollments/" + enrollmentId + "/lessons/" + lessonId, null);
    }

    // ── HTTP ──────────────────────────────────────────────────────────────────

    private String get(String url) {
        try {
            return http().send(HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(60)).GET().build(),
                    HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) { return null; }
    }

    private String post(String url, String body) {
        try {
            return http().send(HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(120))
                    .POST(body != null ? HttpRequest.BodyPublishers.ofString(body)
                            : HttpRequest.BodyPublishers.noBody()).build(),
                    HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) { return null; }
    }

    private void put(String url, String body) {
        try {
            http().send(HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .PUT(body != null ? HttpRequest.BodyPublishers.ofString(body)
                            : HttpRequest.BodyPublishers.noBody()).build(),
                    HttpResponse.BodyHandlers.discarding());
        } catch (Exception ignored) { }
    }

    // ── JSON-P helpers ────────────────────────────────────────────────────────

    private List<JsonObject> array(String json) {
        if (json == null || json.isBlank() || !json.stripLeading().startsWith("[")) return List.of();
        try {
            List<JsonObject> r = new ArrayList<>();
            for (JsonValue v : Json.createReader(new StringReader(json)).readArray()) r.add(v.asJsonObject());
            return r;
        } catch (Exception e) { return List.of(); }
    }

    private JsonObject obj(String json) {
        return Json.createReader(new StringReader(json)).readObject();
    }

    private long num(JsonObject o, String key) {
        return o.containsKey(key) ? o.getJsonNumber(key).longValue() : 0L;
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return (v != null && !v.isBlank()) ? v : fallback;
    }
}
