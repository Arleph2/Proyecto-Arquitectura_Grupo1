package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.content.Lesson;

public class LessonDto {
    private Long id;
    private Long moduleId;
    private String title;
    private String description;
    private Integer position;
    private Integer duration;
    private boolean preview;

    public static LessonDto from(Lesson l) {
        LessonDto dto = new LessonDto();
        dto.id = l.getId();
        dto.moduleId = l.getModule().getId();
        dto.title = l.getTitle();
        dto.description = l.getDescription();
        dto.position = l.getPosition();
        dto.duration = l.getDuration();
        dto.preview = l.isPreview();
        return dto;
    }

    public Long getId() { return id; }
    public Long getModuleId() { return moduleId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getPosition() { return position; }
    public Integer getDuration() { return duration; }
    public boolean isPreview() { return preview; }
}
