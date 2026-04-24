package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.user.LessonProgress;

public class LessonProgressDto {
    private Long id;
    private Long userId;
    private Long lessonId;
    private String status;
    private Double progressPercent;
    private Integer timeSpent;

    public static LessonProgressDto from(LessonProgress lp) {
        LessonProgressDto dto = new LessonProgressDto();
        dto.id = lp.getId();
        dto.userId = lp.getUser().getId();
        dto.lessonId = lp.getLessonId();
        dto.status = lp.getStatus().name();
        dto.progressPercent = lp.getProgressPercent();
        dto.timeSpent = lp.getTimeSpent();
        return dto;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getLessonId() { return lessonId; }
    public String getStatus() { return status; }
    public Double getProgressPercent() { return progressPercent; }
    public Integer getTimeSpent() { return timeSpent; }
}
