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

@ApplicationScoped
public class ServiceClient implements Serializable {

    private static final String CONTENT = env("CONTENT_SERVICE_URL",         "http://content-service:8080");
    private static final String USER    = env("USER_SERVICE_URL",             "http://user-service:8080");
    private static final String RECOMM  = env("RECOMMENDATION_SERVICE_URL",  "http://recommendation-service:8080");
    private static final String SIMUL   = env("SIMULATION_SERVICE_URL",      "http://simulation-service:8080");

    private transient HttpClient http;

    private HttpClient http() {
        if (http == null) {
            http = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
        }
        return http;
    }

    // ── Content Service ───────────────────────────────────────────────────────

    public List<ModuleItem> getModules(long courseId) {
        String json = get(CONTENT + "/api/courses/" + courseId + "/modules");
        return array(json).stream().map(o -> new ModuleItem(
                num(o, "id"), o.getString("title", "")
        )).toList();
    }

    public List<LessonItem> getLessons(long moduleId) {
        String json = get(CONTENT + "/api/modules/" + moduleId + "/lessons");
        return array(json).stream().map(o -> new LessonItem(
                num(o, "id"), o.getString("title", ""), o.getInt("position", 0)
        )).toList();
    }

    public List<ContentItem> getContents(long lessonId) {
        String json = get(CONTENT + "/api/lessons/" + lessonId + "/contents");
        return array(json).stream().map(o -> new ContentItem(
                num(o, "id"), o.getString("type", ""), o.getInt("position", 0)
        )).toList();
    }

    public List<ContentItem> getReinforcementContents(long lessonId) {
        String json = get(CONTENT + "/api/lessons/" + lessonId + "/reinforcement");
        return array(json).stream().map(o -> new ContentItem(
                num(o, "id"), o.getString("type", ""), o.getInt("position", 0)
        )).toList();
    }

    // ── User Service ──────────────────────────────────────────────────────────

    public EnrollmentItem getEnrollment(long enrollmentId) {
        String json = get(USER + "/api/enrollments/" + enrollmentId);
        if (json == null) return null;
        try {
            JsonObject o = obj(json);
            return new EnrollmentItem(
                    num(o, "id"), num(o, "userId"), num(o, "courseId"),
                    o.containsKey("progress") ? o.getJsonNumber("progress").doubleValue() : 0.0);
        } catch (Exception e) { return null; }
    }

    public List<ProgressItem> getProgress(long userId) {
        String json = get(USER + "/api/lesson-progress/user/" + userId);
        return array(json).stream().map(o -> new ProgressItem(
                num(o, "id"), num(o, "lessonId"),
                o.getString("status", ""),
                o.containsKey("timeSpent") ? o.getInt("timeSpent", 0) : 0
        )).toList();
    }

    public List<QuizAttemptItem> getQuizAttempts(long userId) {
        String json = get(USER + "/api/quiz-attempts/user/" + userId);
        return array(json).stream().map(o -> new QuizAttemptItem(
                num(o, "id"), num(o, "quizId"),
                o.containsKey("score")    ? o.getJsonNumber("score").doubleValue()    : 0.0,
                o.containsKey("maxScore") ? o.getJsonNumber("maxScore").doubleValue() : 0.0
        )).toList();
    }

    // ── Simulation Service ────────────────────────────────────────────────────

    public String runSimulation(long enrollmentId) {
        return post(SIMUL + "/api/simulations/enrollments/" + enrollmentId, null);
    }

    // ── Recommendation Service ────────────────────────────────────────────────

    public List<RecommendationItem> getRecommendations(long userId) {
        String json = get(RECOMM + "/api/recommendations/user/" + userId);
        return array(json).stream().map(o -> new RecommendationItem(
                num(o, "id"), num(o, "lessonId"),
                o.containsKey("score") ? o.getJsonNumber("score").doubleValue() : 0.0,
                o.getString("reason", "")
        )).toList();
    }

    public String runAnalysis(long enrollmentId) {
        return post(RECOMM + "/api/analysis/enrollments/" + enrollmentId, null);
    }

    public String runLessonAnalysis(long enrollmentId, long lessonId) {
        return post(RECOMM + "/api/analysis/enrollments/" + enrollmentId + "/lessons/" + lessonId, null);
    }

    // ── HTTP helpers ──────────────────────────────────────────────────────────

    private String get(String url) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(60))
                    .GET().build();
            return http().send(req, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) { return null; }
    }

    private String post(String url, String body) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(120))
                    .POST(body != null
                            ? HttpRequest.BodyPublishers.ofString(body)
                            : HttpRequest.BodyPublishers.noBody())
                    .build();
            return http().send(req, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) { return null; }
    }

    // ── JSON-P helpers ────────────────────────────────────────────────────────

    private List<JsonObject> array(String json) {
        if (json == null || json.isBlank() || !json.stripLeading().startsWith("[")) return List.of();
        try {
            JsonArray arr = Json.createReader(new StringReader(json)).readArray();
            List<JsonObject> result = new ArrayList<>();
            for (JsonValue v : arr) result.add(v.asJsonObject());
            return result;
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
