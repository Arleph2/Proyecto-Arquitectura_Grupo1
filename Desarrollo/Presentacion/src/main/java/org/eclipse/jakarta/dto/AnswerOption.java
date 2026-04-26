package org.eclipse.jakarta.dto;

import java.io.Serializable;

public class AnswerOption implements Serializable {
    private long id;
    private String text;

    public AnswerOption(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public long getId() { return id; }
    public String getText() { return text; }
}
