package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.content.Question;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionDto {
    private Long id;
    private Long quizId;
    private String questionText;
    private String type;
    private List<AnswerDto> answers;

    public static QuestionDto from(Question q) {
        QuestionDto dto = new QuestionDto();
        dto.id = q.getId();
        dto.quizId = q.getQuiz().getId();
        dto.questionText = q.getQuestionText();
        dto.type = q.getType().name();
        dto.answers = q.getAnswers() == null ? List.of()
                : q.getAnswers().stream().map(AnswerDto::from).collect(Collectors.toList());
        return dto;
    }

    public Long getId() { return id; }
    public Long getQuizId() { return quizId; }
    public String getQuestionText() { return questionText; }
    public String getType() { return type; }
    public List<AnswerDto> getAnswers() { return answers; }
}
