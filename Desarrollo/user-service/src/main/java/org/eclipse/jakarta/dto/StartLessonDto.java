package org.eclipse.jakarta.dto;

public class StartLessonDto {
    private Long userId;
    private Long lessonId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
}
