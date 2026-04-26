package org.eclipse.jakarta.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.jakarta.client.ServiceClient;
import org.eclipse.jakarta.dto.*;

import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class CoursePageBean implements Serializable {

    @Inject private ServiceClient client;
    @Inject private CourseBean course;

    private VideoItem   video;
    private ArticleItem article;
    private QuizItem    quiz;

    private VideoItem   reinforcementVideo;
    private ArticleItem reinforcementArticle;
    private QuizItem    reinforcementQuiz;

    private boolean hasReinforcement;

    private Map<Long, String> selectedAnswers = new LinkedHashMap<>();
    private boolean quizSubmitted;

    @PostConstruct
    public void init() { /* view params not yet applied — load in onPreRender */ }

    public void onPreRender(ComponentSystemEvent e) {
        course.initSelection();
        loadLesson();
    }

    private void loadLesson() {
        long lessonId = course.getSelectedLessonId();
        video = null; article = null; quiz = null;
        reinforcementVideo = null; reinforcementArticle = null; reinforcementQuiz = null;
        selectedAnswers.clear();
        quizSubmitted = false;
        hasReinforcement = false;

        if (lessonId <= 0) return;

        for (ContentItem c : client.getContents(lessonId))       load(c, false);
        // only load reinforcement when recommendation-service flagged this lesson as weak
        if (course.isWeakLesson(lessonId)) {
            List<ContentItem> reinf = client.getReinforcementContents(lessonId);
            hasReinforcement = !reinf.isEmpty();
            for (ContentItem c : reinf) load(c, true);
        }

        if (quiz != null)
            for (QuestionAnswerItem q : quiz.getQuestions())
                selectedAnswers.put(q.getId(), "");
    }

    private void load(ContentItem c, boolean reinf) {
        switch (c.getType().toUpperCase()) {
            case "VIDEO"   -> { VideoItem v   = client.getVideo(c.getId());
                                if (reinf) reinforcementVideo   = v;   else video   = v; }
            case "ARTICLE" -> { ArticleItem a = client.getArticle(c.getId());
                                if (reinf) reinforcementArticle = a;   else article = a; }
            case "QUIZ"    -> { QuizItem q    = client.getQuizContent(c.getId());
                                if (reinf) reinforcementQuiz    = q;   else quiz    = q; }
        }
    }

    public void submitQuiz() {
        if (quiz == null) return;
        Map<Long, Long> answers = new LinkedHashMap<>();
        for (Map.Entry<Long, String> e : selectedAnswers.entrySet()) {
            if (e.getValue() != null && !e.getValue().isBlank()) {
                try { answers.put(e.getKey(), Long.parseLong(e.getValue())); }
                catch (NumberFormatException ignored) {}
            }
        }

        client.submitQuiz(course.getUserId(), quiz.getId(), answers);
        quizSubmitted = true;

        long correct = answers.entrySet().stream()
                .filter(e -> e.getValue().equals(client.getCorrectAnswerId(e.getKey())))
                .count();
        int total = quiz.getQuestions().size();
        double pct = total == 0 ? 0 : correct * 100.0 / total;

        FacesContext.getCurrentInstance().addMessage("quizMsg",
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        String.format("%.0f%% — %d de %d correctas", pct, correct, total), null));
    }

    public String markCompleted() {
        long lessonId = course.getSelectedLessonId();
        long userId   = course.getUserId();
        if (lessonId <= 0) return null;

        long progressId = course.getProgressId(lessonId);
        if (progressId < 0) {
            progressId = client.startLesson(userId, lessonId);
        }
        if (progressId > 0) {
            client.completeLesson(progressId);
            // Run analysis after marking complete: now completionScore=1.0 so
            // a low quiz score will create a recommendation and show the reinforcement tab
            client.runLessonAnalysis(course.getEnrollmentId(), lessonId);
            course.refreshProgress();
        }
        return "/course?lessonId=" + lessonId + "&faces-redirect=true";
    }

    public void runLessonAnalysis() {
        client.runLessonAnalysis(course.getEnrollmentId(), course.getSelectedLessonId());
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Análisis ejecutado.", null));
    }

    // getters
    public VideoItem   getVideo()               { return video; }
    public ArticleItem getArticle()             { return article; }
    public QuizItem    getQuiz()                { return quiz; }
    public VideoItem   getReinforcementVideo()  { return reinforcementVideo; }
    public ArticleItem getReinforcementArticle(){ return reinforcementArticle; }
    public QuizItem    getReinforcementQuiz()   { return reinforcementQuiz; }
    public boolean hasVideo()                   { return video != null; }
    public boolean hasArticle()                 { return article != null; }
    public boolean hasQuiz()                    { return quiz != null && quiz.hasQuestions(); }
    public boolean hasReinforcementVideo()      { return reinforcementVideo != null; }
    public boolean hasReinforcementArticle()    { return reinforcementArticle != null; }
    public boolean hasReinforcementQuiz()       { return reinforcementQuiz != null && reinforcementQuiz.hasQuestions(); }
    public boolean isHasReinforcement()         { return hasReinforcement; }
    public boolean isQuizSubmitted()            { return quizSubmitted; }
    public Map<Long, String> getSelectedAnswers() { return selectedAnswers; }
    public void setSelectedAnswers(Map<Long, String> v){ this.selectedAnswers = v; }
}
