package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.content.Content;

public class ContentDto {
    private Long id;
    private Long lessonId;
    private String type;
    private Integer position;

    public static ContentDto from(Content c) {
        ContentDto dto = new ContentDto();
        dto.id = c.getId();
        dto.lessonId = c.getLesson().getId();
        dto.type = c.getType().name();
        dto.position = c.getPosition();
        return dto;
    }

    public Long getId() { return id; }
    public Long getLessonId() { return lessonId; }
    public String getType() { return type; }
    public Integer getPosition() { return position; }
}
