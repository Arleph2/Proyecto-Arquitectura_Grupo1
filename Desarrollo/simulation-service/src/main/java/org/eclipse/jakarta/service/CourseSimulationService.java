package org.eclipse.jakarta.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipse.jakarta.repository.ContentServiceClient;
import org.eclipse.jakarta.repository.RecommendationServiceClient;
import org.eclipse.jakarta.repository.UserServiceClient;
import org.eclipse.jakarta.dto.content.*;
import org.eclipse.jakarta.dto.simulation.*;
import org.eclipse.jakarta.dto.user.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@ApplicationScoped
public class CourseSimulationService {

    @Inject @RestClient private ContentServiceClient contentClient;
    @Inject @RestClient private UserServiceClient userClient;
    @Inject @RestClient private RecommendationServiceClient recommendationClient;

    public SimulationResultDto simulate(Long enrollmentId) {
        EnrollmentDto enrollment = userClient.getEnrollment(enrollmentId);
        CourseDto course = contentClient.getCourse(enrollment.getCourseId());

        List<ModuleDto> modules = contentClient.getModules(enrollment.getCourseId());
        List<ModuleSimulationResultDto> moduleResults = new ArrayList<>(modules.size());

        for (ModuleDto module : modules) {
            moduleResults.add(simulateModule(enrollmentId, enrollment.getUserId(), module));
        }

        userClient.updateProgress(enrollmentId, new UpdateProgressRequestDto(100.0));

        return new SimulationResultDto(enrollmentId, enrollment.getUserId(),
                enrollment.getCourseId(), moduleResults);
    }

    private ModuleSimulationResultDto simulateModule(Long enrollmentId, Long userId, ModuleDto module) {
        List<LessonDto> lessons = contentClient.getLessons(module.getId());
        List<LessonSimulationResultDto> lessonResults = new ArrayList<>(lessons.size());

        for (LessonDto lesson : lessons) {
            lessonResults.add(simulateLesson(enrollmentId, userId, lesson));
        }

        return new ModuleSimulationResultDto(module.getId(), module.getTitle(), lessonResults);
    }

    private LessonSimulationResultDto simulateLesson(Long enrollmentId, Long userId, LessonDto lesson) {
        LessonProgressDto progress = userClient.startLesson(
                new StartLessonRequestDto(userId, lesson.getId()));

        List<ContentDto> contents = contentClient.getContents(lesson.getId());
        List<ContentSimulationResultDto> contentResults = new ArrayList<>(contents.size());
        int totalTimeSpent = 0;

        for (ContentDto content : contents) {
            ContentSimulationResultDto result = simulateContent(userId, content);
            contentResults.add(result);
            totalTimeSpent += result.getTimeSpent();
        }

        userClient.completeLesson(progress.getId(), new CompleteLessonRequestDto(totalTimeSpent));

        try {
            recommendationClient.analyzeLesson(enrollmentId, lesson.getId());
        } catch (Exception e) {
            // recommendation analysis failure must not abort the simulation
        }

        return new LessonSimulationResultDto(lesson.getId(), lesson.getTitle(),
                contentResults, totalTimeSpent);
    }

    private ContentSimulationResultDto simulateContent(Long userId, ContentDto content) {
        return switch (content.getType()) {
            case "VIDEO"   -> simulateVideo(content);
            case "ARTICLE" -> simulateArticle(content);
            case "FILE"    -> simulateFile(content);
            case "QUIZ"    -> simulateQuiz(userId, content);
            default        -> ContentSimulationResultDto.file(content.getId(), 0);
        };
    }

    private ContentSimulationResultDto simulateFile(ContentDto content) {
        int readTime = fetchOrDefault(() -> {
            FileContentDto f = contentClient.getFile(content.getId());
            return f != null ? 60 : 30;
        }, 30);
        return ContentSimulationResultDto.file(content.getId(), readTime);
    }

    private ContentSimulationResultDto simulateVideo(ContentDto content) {
        int duration = fetchOrDefault(() -> {
            VideoContentDto v = contentClient.getVideo(content.getId());
            return v.getDuration() != null ? v.getDuration() : 300;
        }, 300);
        return ContentSimulationResultDto.video(content.getId(), duration);
    }

    private ContentSimulationResultDto simulateArticle(ContentDto content) {
        int readTime = fetchOrDefault(() -> {
            ArticleContentDto a = contentClient.getArticle(content.getId());
            return estimateReadTime(a.getBody());
        }, 180);
        return ContentSimulationResultDto.article(content.getId(), readTime);
    }

    private ContentSimulationResultDto simulateQuiz(Long userId, ContentDto content) {
        QuizDto quiz;
        try {
            quiz = contentClient.getQuiz(content.getId());
        } catch (WebApplicationException e) {
            return ContentSimulationResultDto.quiz(content.getId(), null, 0, 0, 0);
        }

        List<QuestionDto> questions = quiz.getQuestions() != null ? quiz.getQuestions() : List.of();
        List<QuestionAttemptDto> questionAttempts = buildQuestionAttempts(questions);
        int totalTime = questionAttempts.stream()
                .mapToInt(qa -> qa.getTimeSpent() != null ? qa.getTimeSpent() : 0).sum();

        QuizAttemptResponseDto response = userClient.recordQuizAttempt(
                new QuizAttemptRequestDto(userId, quiz.getId(), questionAttempts));

        return ContentSimulationResultDto.quiz(content.getId(), quiz.getId(),
                response.getScore(), response.getMaxScore(), totalTime);
    }

    private List<QuestionAttemptDto> buildQuestionAttempts(List<QuestionDto> questions) {
        return questions.stream().map(q -> {
            Long answerId = fetchOrDefault(() ->
                    contentClient.getCorrectAnswer(q.getId()).getAnswerId(), null);
            int time = 15 + ThreadLocalRandom.current().nextInt(31);
            return new QuestionAttemptDto(q.getId(), answerId, answerId != null, time);
        }).collect(Collectors.toList());
    }

    private <T> T fetchOrDefault(java.util.function.Supplier<T> supplier, T fallback) {
        try {
            return supplier.get();
        } catch (WebApplicationException e) {
            return fallback;
        }
    }

    private int estimateReadTime(String body) {
        if (body == null || body.isBlank()) return 60;
        int words = body.split("\\s+").length;
        return Math.max(60, (words / 200) * 60);
    }
}
