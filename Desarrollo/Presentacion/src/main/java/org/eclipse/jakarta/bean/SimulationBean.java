package org.eclipse.jakarta.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.*;
import org.eclipse.jakarta.client.ServiceClient;
import org.eclipse.jakarta.dto.SimulationModuleItem;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class SimulationBean implements Serializable {

    @Inject
    private ServiceClient client;

    private Long enrollmentId;
    private boolean ran = false;

    private long totalLessons;
    private long totalTimeSpent;
    private double completionPct;
    private long resultUserId;
    private long resultCourseId;
    private List<SimulationModuleItem> moduleResults = new ArrayList<>();

    public void runSimulation() {
        ran = false;
        moduleResults = new ArrayList<>();
        if (enrollmentId == null) { addWarn("Ingrese un ID de enrollment."); return; }

        String json = client.runSimulation(enrollmentId);
        if (json == null) {
            addError("Error al contactar el servicio de simulación. Verifique que el servicio esté activo.");
            return;
        }
        try {
            parse(json);
            ran = true;
        } catch (Exception e) {
            addError("Error procesando la respuesta: " + e.getMessage());
        }
    }

    private void parse(String json) {
        JsonObject root = Json.createReader(new StringReader(json)).readObject();
        resultUserId   = root.containsKey("userId")   ? root.getJsonNumber("userId").longValue()   : 0;
        resultCourseId = root.containsKey("courseId") ? root.getJsonNumber("courseId").longValue() : 0;
        totalLessons   = root.containsKey("totalLessonsCompleted") ? root.getJsonNumber("totalLessonsCompleted").longValue() : 0;
        totalTimeSpent = root.containsKey("totalTimeSpent")        ? root.getJsonNumber("totalTimeSpent").longValue()        : 0;
        completionPct  = root.containsKey("completionPercentage")  ? root.getJsonNumber("completionPercentage").doubleValue() : 0.0;

        JsonArray modules = root.containsKey("modules") ? root.getJsonArray("modules") : Json.createArrayBuilder().build();
        for (JsonValue mv : modules) {
            JsonObject m = mv.asJsonObject();
            int lessonCount = m.containsKey("lessons") ? m.getJsonArray("lessons").size() : 0;
            int time        = m.containsKey("totalTimeSpent") ? m.getInt("totalTimeSpent", 0) : 0;
            moduleResults.add(new SimulationModuleItem(m.getString("title", ""), lessonCount, time));
        }
    }

    public String getFormattedTotalTime() {
        long min = totalTimeSpent / 60;
        long sec = totalTimeSpent % 60;
        return min + "m " + sec + "s";
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

    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }
    public boolean isRan() { return ran; }
    public long getTotalLessons() { return totalLessons; }
    public long getTotalTimeSpent() { return totalTimeSpent; }
    public double getCompletionPct() { return completionPct; }
    public long getResultUserId() { return resultUserId; }
    public long getResultCourseId() { return resultCourseId; }
    public List<SimulationModuleItem> getModuleResults() { return moduleResults; }
}
