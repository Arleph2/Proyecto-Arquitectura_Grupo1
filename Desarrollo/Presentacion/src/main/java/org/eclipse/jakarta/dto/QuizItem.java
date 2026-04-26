package org.eclipse.jakarta.dto;

import java.io.Serializable;
import java.util.List;

public class QuizItem implements Serializable {
    private long id;
    private long contentId;
    private String title;
    private List<QuestionAnswerItem> questions;

    public QuizItem(long id, long contentId, String title, List<QuestionAnswerItem> questions) {
        this.id = id;
        this.contentId = contentId;
        this.title = title;
        this.questions = questions;
    }

    public long getId() { return id; }
    public long getContentId() { return contentId; }
    public String getTitle() { return title; }
    public List<QuestionAnswerItem> getQuestions() { return questions; }
    public boolean hasQuestions() { return questions != null && !questions.isEmpty(); }
}
