package org.eclipse.jakarta.dto;

import java.io.Serializable;

public class LessonItem implements Serializable {
    private long id;
    private String title;
    private int position;

    public LessonItem(long id, String title, int position) {
        this.id = id;
        this.title = title;
        this.position = position;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public int getPosition() { return position; }
}
