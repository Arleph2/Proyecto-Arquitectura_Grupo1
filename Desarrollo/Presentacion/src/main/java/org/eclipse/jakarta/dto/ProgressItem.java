package org.eclipse.jakarta.dto;

import java.io.Serializable;

public class ProgressItem implements Serializable {
    private long id;
    private long lessonId;
    private String status;
    private int timeSpent;

    public ProgressItem(long id, long lessonId, String status, int timeSpent) {
        this.id = id;
        this.lessonId = lessonId;
        this.status = status;
        this.timeSpent = timeSpent;
    }

    public long getId() { return id; }
    public long getLessonId() { return lessonId; }
    public String getStatus() { return status; }
    public int getTimeSpent() { return timeSpent; }
}
