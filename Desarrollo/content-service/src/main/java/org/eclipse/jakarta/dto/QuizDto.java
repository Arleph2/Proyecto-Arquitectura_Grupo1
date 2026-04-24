package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.content.Quiz;
import java.util.List;
import java.util.stream.Collectors;

public class QuizDto {
    private Long id;
    private Long contentId;
    private String title;
    private List<QuestionDto> questions;

    public static QuizDto from(Quiz q) {
        QuizDto dto = new QuizDto();
        dto.id = q.getId();
        dto.contentId = q.getContent().getId();
        dto.title = q.getTitle();
        dto.questions = q.getQuestions() == null ? List.of()
                : q.getQuestions().stream().map(QuestionDto::from).collect(Collectors.toList());
        return dto;
    }

    public Long getId() { return id; }
    public Long getContentId() { return contentId; }
    public String getTitle() { return title; }
    public List<QuestionDto> getQuestions() { return questions; }
}
