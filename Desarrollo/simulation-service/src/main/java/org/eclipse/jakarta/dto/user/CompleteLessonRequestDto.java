package org.eclipse.jakarta.dto.user;

public class CompleteLessonRequestDto {
    private Integer timeSpent;

    public CompleteLessonRequestDto() {}

    public CompleteLessonRequestDto(Integer timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Integer getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Integer timeSpent) { this.timeSpent = timeSpent; }
}
