package org.eclipse.jakarta.dto.analysis;

import org.eclipse.jakarta.dto.RecommendationDto;
import java.util.List;

public class AnalysisResultDto {
    private Long enrollmentId;
    private Long userId;
    private Long courseId;
    private List<ModuleAnalysisDto> modules;
    private List<RecommendationDto> recommendations;

    public AnalysisResultDto() {}

    public AnalysisResultDto(Long enrollmentId, Long userId, Long courseId,
                              List<ModuleAnalysisDto> modules,
                              List<RecommendationDto> recommendations) {
        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.courseId = courseId;
        this.modules = modules;
        this.recommendations = recommendations;
    }

    public Long getEnrollmentId() { return enrollmentId; }
    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }
    public List<ModuleAnalysisDto> getModules() { return modules; }
    public List<RecommendationDto> getRecommendations() { return recommendations; }
}
