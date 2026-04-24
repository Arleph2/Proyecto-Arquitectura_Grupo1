package org.eclipse.jakarta.dto.simulation;

import java.util.List;

public class SimulationResultDto {
    private final Long enrollmentId;
    private final Long userId;
    private final Long courseId;
    private final List<ModuleSimulationResultDto> modules;
    private final int totalLessonsCompleted;
    private final int totalTimeSpent;
    private final double completionPercentage;

    public SimulationResultDto(Long enrollmentId, Long userId, Long courseId,
                                List<ModuleSimulationResultDto> modules) {
        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.courseId = courseId;
        this.modules = modules;
        this.totalLessonsCompleted = modules.stream()
                .mapToInt(m -> m.getLessons().size()).sum();
        this.totalTimeSpent = modules.stream()
                .mapToInt(ModuleSimulationResultDto::getTotalTimeSpent).sum();
        this.completionPercentage = 100.0;
    }

    public Long getEnrollmentId() { return enrollmentId; }
    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }
    public List<ModuleSimulationResultDto> getModules() { return modules; }
    public int getTotalLessonsCompleted() { return totalLessonsCompleted; }
    public int getTotalTimeSpent() { return totalTimeSpent; }
    public double getCompletionPercentage() { return completionPercentage; }
}
