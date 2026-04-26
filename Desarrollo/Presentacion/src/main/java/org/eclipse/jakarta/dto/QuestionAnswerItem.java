package org.eclipse.jakarta.dto;

import java.io.Serializable;
import java.util.List;

public class QuestionAnswerItem implements Serializable {
    private long id;
    private String questionText;
    private List<AnswerOption> answers;

    public QuestionAnswerItem(long id, String questionText, List<AnswerOption> answers) {
        this.id = id;
        this.questionText = questionText;
        this.answers = answers;
    }

    public long getId() { return id; }
    public String getQuestionText() { return questionText; }
    public List<AnswerOption> getAnswers() { return answers; }
}
