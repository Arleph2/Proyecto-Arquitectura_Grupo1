package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.content.Course;

public class CourseDto {
    private Long id;
    private String title;
    private String description;
    private String level;
    private String language;
    private boolean published;

    public static CourseDto from(Course c) {
        CourseDto dto = new CourseDto();
        dto.id = c.getId();
        dto.title = c.getTitle();
        dto.description = c.getDescription();
        dto.level = c.getLevel();
        dto.language = c.getLanguage();
        dto.published = c.isPublished();
        return dto;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLevel() { return level; }
    public String getLanguage() { return language; }
    public boolean isPublished() { return published; }
}
