package org.eclipse.jakarta.dto.simulation;

import java.util.List;

public class LessonSimulationResultDto {
    private final Long lessonId;
    private final String title;
    private final List<ContentSimulationResultDto> contents;
    private final int timeSpent;

    public LessonSimulationResultDto(Long lessonId, String title,
                                      List<ContentSimulationResultDto> contents, int timeSpent) {
        this.lessonId = lessonId;
        this.title = title;
        this.contents = contents;
        this.timeSpent = timeSpent;
    }

    public Long getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public List<ContentSimulationResultDto> getContents() { return contents; }
    public int getTimeSpent() { return timeSpent; }
}
