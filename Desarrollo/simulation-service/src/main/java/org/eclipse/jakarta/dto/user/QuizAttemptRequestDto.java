package org.eclipse.jakarta.dto.user;

import java.util.List;

public class QuizAttemptRequestDto {
    private Long userId;
    private Long quizId;
    private List<QuestionAttemptDto> questionAttempts;

    public QuizAttemptRequestDto() {}

    public QuizAttemptRequestDto(Long userId, Long quizId, List<QuestionAttemptDto> questionAttempts) {
        this.userId = userId;
        this.quizId = quizId;
        this.questionAttempts = questionAttempts;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    public List<QuestionAttemptDto> getQuestionAttempts() { return questionAttempts; }
    public void setQuestionAttempts(List<QuestionAttemptDto> questionAttempts) { this.questionAttempts = questionAttempts; }
}
