package org.eclipse.jakarta.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipse.jakarta.repository.RecommendationRepository;
import org.eclipse.jakarta.repository.ContentServiceClient;
import org.eclipse.jakarta.repository.UserServiceClient;
import org.eclipse.jakarta.dto.analysis.AnalysisResultDto;
import org.eclipse.jakarta.dto.analysis.ContentSummaryDto;
import org.eclipse.jakarta.dto.analysis.LessonAnalysisDto;
import org.eclipse.jakarta.dto.analysis.ModuleAnalysisDto;
import org.eclipse.jakarta.dto.content.ContentDto;
import org.eclipse.jakarta.dto.content.LessonDto;
import org.eclipse.jakarta.dto.content.ModuleDto;
import org.eclipse.jakarta.dto.content.QuizDto;
import org.eclipse.jakarta.dto.user.EnrollmentDto;
import org.eclipse.jakarta.dto.user.LessonProgressDto;
import org.eclipse.jakarta.dto.user.QuizAttemptDto;
import org.eclipse.jakarta.dto.RecommendationDto;
import org.eclipse.jakarta.entity.Recommendation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Stateless
public class RecommendationService {

    private static final double WEAKNESS_THRESHOLD = 0.70;

    @EJB private RecommendationRepository recommendationRepository;
    @Inject @RestClient private ContentServiceClient contentClient;
    @Inject @RestClient private UserServiceClient userClient;

    public AnalysisResultDto analyze(Long enrollmentId) {
        EnrollmentDto enrollment = userClient.getEnrollment(enrollmentId);
        Long userId = enrollment.getUserId();
        Long courseId = enrollment.getCourseId();

        Map<Long, LessonProgressDto> progressByLessonId = buildProgressIndex(userId);
        Map<Long, List<QuizAttemptDto>> attemptsByQuizId = buildAttemptsIndex(userId);

        List<ModuleDto> modules = contentClient.getModules(courseId);
        List<ModuleAnalysisDto> moduleAnalyses = new ArrayList<>();
        List<RecommendationDto> recommendations = new ArrayList<>();

        for (ModuleDto module : modules) {
            List<LessonDto> moduleLessons = contentClient.getLessons(module.getId());
            List<LessonAnalysisDto> lessonAnalyses = new ArrayList<>();

            for (LessonDto lesson : moduleLessons) {
                LessonAnalysisDto analysis = analyzeLesson(
                        lesson, progressByLessonId, attemptsByQuizId);
                lessonAnalyses.add(analysis);

                if (analysis.isWeak()) {
                    recommendations.add(upsertRecommendation(userId, lesson, analysis));
                } else {
                    recommendationRepository.deleteByUserIdAndLessonId(userId, lesson.getId());
                }
            }

            double avgScore = lessonAnalyses.stream()
                    .mapToDouble(LessonAnalysisDto::getOverallScore)
                    .average().orElse(0.0);
            moduleAnalyses.add(new ModuleAnalysisDto(module.getId(), module.getTitle(),
                    avgScore, lessonAnalyses));
        }

        return new AnalysisResultDto(enrollmentId, userId, courseId, moduleAnalyses, recommendations);
    }

    public LessonAnalysisDto analyzeLesson(Long enrollmentId, Long lessonId) {
        EnrollmentDto enrollment = userClient.getEnrollment(enrollmentId);
        Long userId = enrollment.getUserId();

        LessonDto lesson = contentClient.getLesson(lessonId);

        Map<Long, LessonProgressDto> progressByLessonId = buildProgressIndex(userId);
        Map<Long, List<QuizAttemptDto>> attemptsByQuizId = buildAttemptsIndex(userId);

        LessonAnalysisDto analysis = analyzeLesson(
                lesson, progressByLessonId, attemptsByQuizId);

        if (analysis.isWeak()) {
            upsertRecommendation(userId, lesson, analysis);
        } else {
            recommendationRepository.deleteByUserIdAndLessonId(userId, lessonId);
        }

        return analysis;
    }

    public List<RecommendationDto> findByUserId(Long userId) {
        return recommendationRepository.findByUserId(userId).stream()
                .map(RecommendationDto::from)
                .collect(Collectors.toList());
    }

    private LessonAnalysisDto analyzeLesson(LessonDto lesson,
            Map<Long, LessonProgressDto> progressByLessonId,
            Map<Long, List<QuizAttemptDto>> attemptsByQuizId) {

        LessonProgressDto progress = progressByLessonId.get(lesson.getId());
        double completionScore = (progress != null && "COMPLETED".equals(progress.getStatus())) ? 1.0 : 0.0;

        List<ContentDto> contents = contentClient.getContents(lesson.getId());

        List<ContentDto> quizContents = contents.stream()
                .filter(c -> "QUIZ".equals(c.getType()))
                .collect(Collectors.toList());

        double quizScore = quizContents.isEmpty()
                ? completionScore
                : quizContents.stream()
                        .mapToDouble(c -> bestQuizRatio(c, attemptsByQuizId))
                        .max().orElse(0.0);

        double overallScore = quizContents.isEmpty()
                ? completionScore
                : (completionScore + quizScore) / 2.0;

        // Only flag as weak if the lesson was completed AND the quiz score is below threshold
        boolean weak = completionScore >= 1.0 && !quizContents.isEmpty() && quizScore < WEAKNESS_THRESHOLD;

        List<ContentSummaryDto> recommended = contentClient.getReinforcementContents(lesson.getId())
                .stream()
                .map(c -> new ContentSummaryDto(c.getId(), c.getType()))
                .collect(Collectors.toList());

        return new LessonAnalysisDto(lesson.getId(), lesson.getTitle(),
                completionScore, quizScore, overallScore,
                weak, recommended);
    }

    private double bestQuizRatio(ContentDto quizContent,
            Map<Long, List<QuizAttemptDto>> attemptsByQuizId) {
        QuizDto quiz = fetchOrNull(() -> contentClient.getQuiz(quizContent.getId()));
        if (quiz == null) return 0.0;
        List<QuizAttemptDto> attempts = attemptsByQuizId.getOrDefault(quiz.getId(), List.of());
        return attempts.stream()
                .filter(a -> a.getMaxScore() != null && a.getMaxScore() > 0)
                .mapToDouble(a -> a.getScore() / a.getMaxScore())
                .max().orElse(0.0);
    }

    private RecommendationDto upsertRecommendation(Long userId, LessonDto lesson,
            LessonAnalysisDto analysis) {
        recommendationRepository.deleteByUserIdAndLessonId(userId, lesson.getId());
        Recommendation r = new Recommendation();
        r.setUserId(userId);
        r.setLessonId(lesson.getId());
        r.setScore(analysis.getOverallScore());
        r.setReason(buildReason(lesson.getTitle(), analysis.getCompletionScore(), analysis.getQuizScore()));
        recommendationRepository.save(r);
        return RecommendationDto.from(r);
    }

    private String buildReason(String lessonTitle, double completionScore, double quizScore) {
        if (completionScore < 1.0 && quizScore < WEAKNESS_THRESHOLD) {
            return "Lesson '" + lessonTitle + "' was not completed and quiz performance was below threshold.";
        } else if (completionScore < 1.0) {
            return "Lesson '" + lessonTitle + "' was not completed.";
        } else {
            return "Quiz performance for lesson '" + lessonTitle + "' was below the expected threshold.";
        }
    }

    private Map<Long, LessonProgressDto> buildProgressIndex(Long userId) {
        return userClient.getLessonProgressByUser(userId).stream()
                .collect(Collectors.toMap(LessonProgressDto::getLessonId, p -> p, (a, b) -> a));
    }

    private Map<Long, List<QuizAttemptDto>> buildAttemptsIndex(Long userId) {
        return userClient.getQuizAttemptsByUser(userId).stream()
                .collect(Collectors.groupingBy(QuizAttemptDto::getQuizId));
    }

    private <T> T fetchOrNull(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (WebApplicationException e) {
            return null;
        }
    }
}
