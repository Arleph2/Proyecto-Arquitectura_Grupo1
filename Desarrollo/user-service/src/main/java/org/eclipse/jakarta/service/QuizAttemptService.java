package org.eclipse.jakarta.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.eclipse.jakarta.repository.QuizAttemptRepository;
import org.eclipse.jakarta.repository.UserRepository;
import org.eclipse.jakarta.dto.QuestionAttemptDto;
import org.eclipse.jakarta.dto.QuizAttemptRequestDto;
import org.eclipse.jakarta.dto.QuizAttemptResponseDto;
import org.eclipse.jakarta.entity.user.QuestionAttempt;
import org.eclipse.jakarta.entity.user.QuizAttempt;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class QuizAttemptService {

    @EJB private QuizAttemptRepository quizAttemptRepository;
    @EJB private UserRepository userRepository;

    public QuizAttemptResponseDto recordAttempt(QuizAttemptRequestDto request) {
        long previousAttempts = quizAttemptRepository.countByUserIdAndQuizId(
                request.getUserId(), request.getQuizId());

        QuizAttempt attempt = buildAttempt(request, previousAttempts);
        quizAttemptRepository.save(attempt);

        List<QuestionAttempt> questionAttempts = buildQuestionAttempts(attempt, request.getQuestionAttempts());
        quizAttemptRepository.saveQuestionAttempts(questionAttempts);

        return QuizAttemptResponseDto.from(attempt);
    }

    private QuizAttempt buildAttempt(QuizAttemptRequestDto request, long previousAttempts) {
        long correctCount = request.getQuestionAttempts().stream()
                .filter(qa -> Boolean.TRUE.equals(qa.getCorrect()))
                .count();

        int totalTime = request.getQuestionAttempts().stream()
                .mapToInt(qa -> qa.getTimeSpent() != null ? qa.getTimeSpent() : 0)
                .sum();

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(userRepository.getReference(request.getUserId()));
        attempt.setQuizId(request.getQuizId());
        attempt.setAttemptNumber((int) previousAttempts + 1);
        attempt.setScore((double) correctCount);
        attempt.setMaxScore((double) request.getQuestionAttempts().size());
        attempt.setTimeSpent(totalTime);
        return attempt;
    }

    private List<QuestionAttempt> buildQuestionAttempts(QuizAttempt attempt,
                                                         List<QuestionAttemptDto> dtos) {
        return dtos.stream().map(dto -> {
            QuestionAttempt qa = new QuestionAttempt();
            qa.setQuizAttempt(attempt);
            qa.setQuestionId(dto.getQuestionId());
            qa.setSelectedAnswerId(dto.getSelectedAnswerId());
            qa.setCorrect(dto.getCorrect());
            qa.setTimeSpent(dto.getTimeSpent());
            return qa;
        }).collect(Collectors.toList());
    }
}
