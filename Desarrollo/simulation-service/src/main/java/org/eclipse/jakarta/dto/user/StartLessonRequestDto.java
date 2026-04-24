package org.eclipse.jakarta.dto.user;

public class StartLessonRequestDto {
    private Long userId;
    private Long lessonId;

    public StartLessonRequestDto() {}

    public StartLessonRequestDto(Long userId, Long lessonId) {
        this.userId = userId;
        this.lessonId = lessonId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
}
