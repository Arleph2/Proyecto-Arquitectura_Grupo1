package org.eclipse.jakarta.dto.simulation;

import java.util.List;

public class ModuleSimulationResultDto {
    private final Long moduleId;
    private final String title;
    private final List<LessonSimulationResultDto> lessons;
    private final int totalTimeSpent;

    public ModuleSimulationResultDto(Long moduleId, String title,
                                      List<LessonSimulationResultDto> lessons) {
        this.moduleId = moduleId;
        this.title = title;
        this.lessons = lessons;
        this.totalTimeSpent = lessons.stream().mapToInt(LessonSimulationResultDto::getTimeSpent).sum();
    }

    public Long getModuleId() { return moduleId; }
    public String getTitle() { return title; }
    public List<LessonSimulationResultDto> getLessons() { return lessons; }
    public int getTotalTimeSpent() { return totalTimeSpent; }
}
