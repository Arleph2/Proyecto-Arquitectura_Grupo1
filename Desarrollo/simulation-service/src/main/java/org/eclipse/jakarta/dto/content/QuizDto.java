package org.eclipse.jakarta.dto.content;

import java.util.List;

public class QuizDto {
    private Long id;
    private String title;
    private List<QuestionDto> questions;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<QuestionDto> getQuestions() { return questions; }
    public void setQuestions(List<QuestionDto> questions) { this.questions = questions; }
}
