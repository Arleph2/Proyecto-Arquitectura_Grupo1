package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.content.Module;

public class ModuleDto {
    private Long id;
    private Long courseId;
    private String title;
    private Integer position;

    public static ModuleDto from(Module m) {
        ModuleDto dto = new ModuleDto();
        dto.id = m.getId();
        dto.courseId = m.getCourse().getId();
        dto.title = m.getTitle();
        dto.position = m.getPosition();
        return dto;
    }

    public Long getId() { return id; }
    public Long getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public Integer getPosition() { return position; }
}
