package org.eclipse.jakarta.dto;

public class QuestionAttemptDto {
    private Long questionId;
    private Long selectedAnswerId;
    private Boolean correct;
    private Integer timeSpent;

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getSelectedAnswerId() { return selectedAnswerId; }
    public void setSelectedAnswerId(Long selectedAnswerId) { this.selectedAnswerId = selectedAnswerId; }
    public Boolean getCorrect() { return correct; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
    public Integer getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Integer timeSpent) { this.timeSpent = timeSpent; }
}
