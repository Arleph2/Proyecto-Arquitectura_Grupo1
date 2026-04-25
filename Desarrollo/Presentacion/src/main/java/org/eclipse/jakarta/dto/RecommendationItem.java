package org.eclipse.jakarta.dto;

import java.io.Serializable;

public class RecommendationItem implements Serializable {
    private long id;
    private long lessonId;
    private double score;
    private String reason;

    public RecommendationItem(long id, long lessonId, double score, String reason) {
        this.id = id;
        this.lessonId = lessonId;
        this.score = score;
        this.reason = reason;
    }

    public long getId() { return id; }
    public long getLessonId() { return lessonId; }
    public double getScore() { return score; }
    public String getReason() { return reason; }
    public String getScorePct() { return String.format("%.0f%%", score * 100); }
}
