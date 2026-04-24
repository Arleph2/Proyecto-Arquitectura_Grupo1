package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.content.Question;

public class QuestionDto {
    private Long id;
    private Long quizId;
    private String questionText;
    private String type;

    public static QuestionDto from(Question q) {
        QuestionDto dto = new QuestionDto();
        dto.id = q.getId();
        dto.quizId = q.getQuiz().getId();
        dto.questionText = q.getQuestionText();
        dto.type = q.getType().name();
        return dto;
    }

    public Long getId() { return id; }
    public Long getQuizId() { return quizId; }
    public String getQuestionText() { return questionText; }
    public String getType() { return type; }
}
