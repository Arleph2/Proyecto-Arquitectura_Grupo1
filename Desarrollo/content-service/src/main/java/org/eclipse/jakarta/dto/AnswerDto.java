package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.content.Answer;

public class AnswerDto {
    private Long id;
    private String answerText;

    public static AnswerDto from(Answer a) {
        AnswerDto dto = new AnswerDto();
        dto.id = a.getId();
        dto.answerText = a.getAnswerText();
        return dto;
    }

    public Long getId() { return id; }
    public String getAnswerText() { return answerText; }
}
