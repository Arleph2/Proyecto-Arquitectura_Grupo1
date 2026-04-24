package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.user.Enrollment;

public class EnrollmentDto {
    private Long id;
    private Long userId;
    private Long courseId;
    private Double progress;

    public static EnrollmentDto from(Enrollment e) {
        EnrollmentDto dto = new EnrollmentDto();
        dto.id = e.getId();
        dto.userId = e.getUser().getId();
        dto.courseId = e.getCourseId();
        dto.progress = e.getProgress();
        return dto;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }
    public Double getProgress() { return progress; }
}
