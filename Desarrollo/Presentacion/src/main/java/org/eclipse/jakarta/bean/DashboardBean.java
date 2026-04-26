package org.eclipse.jakarta.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.jakarta.client.ServiceClient;
import org.eclipse.jakarta.dto.*;

import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    @Inject private ServiceClient client;
    @Inject private CourseBean course;

    private List<RecommendationItem> recommendations = new ArrayList<>();
    private List<QuizAttemptItem>    attempts        = new ArrayList<>();
    private String simulationResult;

    @PostConstruct
    public void init() {
        long userId = course.getUserId();
        if (userId > 0) {
            recommendations = client.getRecommendations(userId);
            attempts        = client.getQuizAttempts(userId);
        }
    }

    public void runSimulation() {
        simulationResult = client.runSimulation(course.getEnrollmentId());
        course.loadEnrollment();
        recommendations = client.getRecommendations(course.getUserId());
        attempts        = client.getQuizAttempts(course.getUserId());
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Simulación completada.", null));
    }

    public void runAnalysis() {
        client.runAnalysis(course.getEnrollmentId());
        recommendations = client.getRecommendations(course.getUserId());
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Análisis ejecutado.", null));
    }

    public String getAvgScoreLabel() {
        if (attempts.isEmpty()) return "-";
        double avg = attempts.stream().mapToDouble(QuizAttemptItem::getRatio).average().orElse(0) * 100;
        return (int) avg + "%";
    }

    public List<RecommendationItem> getRecommendations() { return recommendations; }
    public List<QuizAttemptItem>    getAttempts()         { return attempts; }
    public String                   getSimulationResult() { return simulationResult; }
}
