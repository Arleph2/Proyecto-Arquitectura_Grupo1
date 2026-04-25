package org.eclipse.jakarta.dto;

import java.io.Serializable;

public class EnrollmentItem implements Serializable {
    private long id;
    private long userId;
    private long courseId;
    private double progress;

    public EnrollmentItem(long id, long userId, long courseId, double progress) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.progress = progress;
    }

    public long getId() { return id; }
    public long getUserId() { return userId; }
    public long getCourseId() { return courseId; }
    public double getProgress() { return progress; }
}
