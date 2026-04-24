package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.user.QuizAttempt;

public class QuizAttemptResponseDto {
    private Long id;
    private Long userId;
    private Long quizId;
    private Double score;
    private Double maxScore;
    private Integer attemptNumber;
    private Integer timeSpent;

    public static QuizAttemptResponseDto from(QuizAttempt qa) {
        QuizAttemptResponseDto dto = new QuizAttemptResponseDto();
        dto.id = qa.getId();
        dto.userId = qa.getUser().getId();
        dto.quizId = qa.getQuizId();
        dto.score = qa.getScore();
        dto.maxScore = qa.getMaxScore();
        dto.attemptNumber = qa.getAttemptNumber();
        dto.timeSpent = qa.getTimeSpent();
        return dto;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getQuizId() { return quizId; }
    public Double getScore() { return score; }
    public Double getMaxScore() { return maxScore; }
    public Integer getAttemptNumber() { return attemptNumber; }
    public Integer getTimeSpent() { return timeSpent; }
}
