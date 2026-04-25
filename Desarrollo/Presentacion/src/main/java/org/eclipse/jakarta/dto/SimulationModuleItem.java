package org.eclipse.jakarta.dto;

import java.io.Serializable;

public class SimulationModuleItem implements Serializable {
    private String title;
    private int lessonCount;
    private int totalTimeSpent;

    public SimulationModuleItem(String title, int lessonCount, int totalTimeSpent) {
        this.title = title;
        this.lessonCount = lessonCount;
        this.totalTimeSpent = totalTimeSpent;
    }

    public String getTitle() { return title; }
    public int getLessonCount() { return lessonCount; }
    public int getTotalTimeSpent() { return totalTimeSpent; }
    public String getFormattedTime() {
        int min = totalTimeSpent / 60;
        int sec = totalTimeSpent % 60;
        return min + "m " + sec + "s";
    }
}
