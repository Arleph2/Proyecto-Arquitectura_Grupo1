package org.eclipse.jakarta.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.*;
import org.eclipse.jakarta.client.ServiceClient;
import org.eclipse.jakarta.dto.AnalysisItem;
import org.eclipse.jakarta.dto.ContentItem;
import org.eclipse.jakarta.dto.RecommendationItem;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class RecommendationBean implements Serializable {

    @Inject
    private ServiceClient client;

    // ── Recommendations section ───────────────────────────────────────────────
    private Long recoUserId;
    private List<RecommendationItem> recommendations = new ArrayList<>();

    // ── Full analysis section ─────────────────────────────────────────────────
    private Long analysisEnrollmentId;
    private boolean analysisRan = false;
    private int totalWeak;
    private int totalRecommendations;
    private List<AnalysisItem> weakLessons = new ArrayList<>();

    // ── Lesson analysis section ───────────────────────────────────────────────
    private Long lessonEnrollmentId;
    private Long lessonId;
    private boolean lessonAnalysisRan = false;
    private AnalysisItem lessonResult;

    // ── Actions ───────────────────────────────────────────────────────────────

    public void loadRecommendations() {
        recommendations = new ArrayList<>();
        if (recoUserId == null) { addWarn("Ingrese un ID de usuario."); return; }
        recommendations = client.getRecommendations(recoUserId);
    }

    public void runFullAnalysis() {
        weakLessons = new ArrayList<>();
        analysisRan = false;
        if (analysisEnrollmentId == null) { addWarn("Ingrese un ID de enrollment."); return; }

        String json = client.runAnalysis(analysisEnrollmentId);
        if (json == null) { addError("Error al ejecutar el análisis."); return; }

        try {
            JsonObject root = Json.createReader(new StringReader(json)).readObject();
            JsonArray modules = root.containsKey("modules") ? root.getJsonArray("modules") : Json.createArrayBuilder().build();
            for (JsonValue mv : modules) {
                JsonArray lessons = mv.asJsonObject().containsKey("lessons")
                        ? mv.asJsonObject().getJsonArray("lessons") : Json.createArrayBuilder().build();
                for (JsonValue lv : lessons) {
                    AnalysisItem item = parseLesson(lv.asJsonObject());
                    if (item.isWeak()) weakLessons.add(item);
                }
            }
            totalWeak = weakLessons.size();
            totalRecommendations = root.containsKey("recommendations")
                    ? root.getJsonArray("recommendations").size() : 0;
            analysisRan = true;
        } catch (Exception e) {
            addError("Error procesando el análisis: " + e.getMessage());
        }
    }

    public void runLessonAnalysis() {
        lessonResult = null;
        lessonAnalysisRan = false;
        if (lessonEnrollmentId == null || lessonId == null) {
            addWarn("Ingrese enrollment ID y lesson ID.");
            return;
        }
        String json = client.runLessonAnalysis(lessonEnrollmentId, lessonId);
        if (json == null) { addError("Error al ejecutar el análisis de lección."); return; }

        try {
            lessonResult = parseLesson(Json.createReader(new StringReader(json)).readObject());
            lessonAnalysisRan = true;
        } catch (Exception e) {
            addError("Error procesando la respuesta: " + e.getMessage());
        }
    }

    private AnalysisItem parseLesson(JsonObject o) {
        List<ContentItem> recommended = new ArrayList<>();
        if (o.containsKey("recommendedContents")) {
            for (JsonValue cv : o.getJsonArray("recommendedContents")) {
                JsonObject co = cv.asJsonObject();
                recommended.add(new ContentItem(
                        co.containsKey("id") ? co.getJsonNumber("id").longValue() : 0L,
                        co.getString("type", ""), 0));
            }
        }
        String title = o.containsKey("lessonTitle") ? o.getString("lessonTitle", "")
                     : o.getString("title", "");
        return new AnalysisItem(
                o.containsKey("lessonId") ? o.getJsonNumber("lessonId").longValue() : 0L,
                title,
                o.containsKey("completionScore") ? o.getJsonNumber("completionScore").doubleValue() : 0.0,
                o.containsKey("quizScore")        ? o.getJsonNumber("quizScore").doubleValue()        : 0.0,
                o.containsKey("overallScore")     ? o.getJsonNumber("overallScore").doubleValue()     : 0.0,
                o.getBoolean("weak", false),
                recommended);
    }

    private void addWarn(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", msg));
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", msg));
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getRecoUserId() { return recoUserId; }
    public void setRecoUserId(Long recoUserId) { this.recoUserId = recoUserId; }
    public List<RecommendationItem> getRecommendations() { return recommendations; }

    public Long getAnalysisEnrollmentId() { return analysisEnrollmentId; }
    public void setAnalysisEnrollmentId(Long analysisEnrollmentId) { this.analysisEnrollmentId = analysisEnrollmentId; }
    public boolean isAnalysisRan() { return analysisRan; }
    public int getTotalWeak() { return totalWeak; }
    public int getTotalRecommendations() { return totalRecommendations; }
    public List<AnalysisItem> getWeakLessons() { return weakLessons; }

    public Long getLessonEnrollmentId() { return lessonEnrollmentId; }
    public void setLessonEnrollmentId(Long lessonEnrollmentId) { this.lessonEnrollmentId = lessonEnrollmentId; }
    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
    public boolean isLessonAnalysisRan() { return lessonAnalysisRan; }
    public AnalysisItem getLessonResult() { return lessonResult; }
}
