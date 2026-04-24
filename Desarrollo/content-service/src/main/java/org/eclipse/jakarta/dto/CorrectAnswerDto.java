package org.eclipse.jakarta.dto;

public class CorrectAnswerDto {
    private Long questionId;
    private Long answerId;

    public CorrectAnswerDto() {}

    public CorrectAnswerDto(Long questionId, Long answerId) {
        this.questionId = questionId;
        this.answerId = answerId;
    }

    public Long getQuestionId() { return questionId; }
    public Long getAnswerId() { return answerId; }
}
