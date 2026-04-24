package org.eclipse.jakarta.dto.user;

public class QuizAttemptResponseDto {
    private Long id;
    private Double score;
    private Double maxScore;
    private Integer timeSpent;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }
    public Integer getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Integer timeSpent) { this.timeSpent = timeSpent; }
}
