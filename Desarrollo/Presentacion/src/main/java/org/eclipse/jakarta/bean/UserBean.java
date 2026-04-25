package org.eclipse.jakarta.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.jakarta.client.ServiceClient;
import org.eclipse.jakarta.dto.EnrollmentItem;
import org.eclipse.jakarta.dto.ProgressItem;
import org.eclipse.jakarta.dto.QuizAttemptItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class UserBean implements Serializable {

    @Inject
    private ServiceClient client;

    private Long enrollmentId;
    private EnrollmentItem enrollment;

    private Long progressUserId;
    private List<ProgressItem> progressList = new ArrayList<>();

    private Long attemptsUserId;
    private List<QuizAttemptItem> quizAttempts = new ArrayList<>();

    public void lookupEnrollment() {
        enrollment = null;
        if (enrollmentId == null) { addWarn("Ingrese un ID de enrollment."); return; }

        enrollment = client.getEnrollment(enrollmentId);
        if (enrollment == null) {
            addWarn("No se encontró el enrollment con ID " + enrollmentId);
        }
    }

    public void loadProgress() {
        progressList = new ArrayList<>();
        if (progressUserId == null) { addWarn("Ingrese un ID de usuario."); return; }

        progressList = client.getProgress(progressUserId);
        if (progressList.isEmpty()) {
            addInfo("El usuario " + progressUserId + " no tiene progreso registrado.");
        }
    }

    public void loadQuizAttempts() {
        quizAttempts = new ArrayList<>();
        if (attemptsUserId == null) { addWarn("Ingrese un ID de usuario."); return; }

        quizAttempts = client.getQuizAttempts(attemptsUserId);
        if (quizAttempts.isEmpty()) {
            addInfo("El usuario " + attemptsUserId + " no tiene intentos de quiz registrados.");
        }
    }

    private void addWarn(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", msg));
    }

    private void addInfo(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", msg));
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }
    public EnrollmentItem getEnrollment() { return enrollment; }

    public Long getProgressUserId() { return progressUserId; }
    public void setProgressUserId(Long progressUserId) { this.progressUserId = progressUserId; }
    public List<ProgressItem> getProgressList() { return progressList; }

    public Long getAttemptsUserId() { return attemptsUserId; }
    public void setAttemptsUserId(Long attemptsUserId) { this.attemptsUserId = attemptsUserId; }
    public List<QuizAttemptItem> getQuizAttempts() { return quizAttempts; }
}
