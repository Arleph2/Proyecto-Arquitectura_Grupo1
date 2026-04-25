package org.eclipse.jakarta.dto.analysis;

import java.util.List;

public class ModuleAnalysisDto {
    private Long moduleId;
    private String moduleTitle;
    private Double averageScore;
    private List<LessonAnalysisDto> lessons;

    public ModuleAnalysisDto() {}

    public ModuleAnalysisDto(Long moduleId, String moduleTitle,
                              Double averageScore, List<LessonAnalysisDto> lessons) {
        this.moduleId = moduleId;
        this.moduleTitle = moduleTitle;
        this.averageScore = averageScore;
        this.lessons = lessons;
    }

    public Long getModuleId() { return moduleId; }
    public String getModuleTitle() { return moduleTitle; }
    public Double getAverageScore() { return averageScore; }
    public List<LessonAnalysisDto> getLessons() { return lessons; }
}
