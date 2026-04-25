package org.eclipse.jakarta.dto;

import java.io.Serializable;
import java.util.List;

public class AnalysisItem implements Serializable {
    private long lessonId;
    private String lessonTitle;
    private double completionScore;
    private double quizScore;
    private double overallScore;
    private boolean weak;
    private List<ContentItem> recommendedContents;

    public AnalysisItem(long lessonId, String lessonTitle, double completionScore,
                        double quizScore, double overallScore, boolean weak,
                        List<ContentItem> recommendedContents) {
        this.lessonId = lessonId;
        this.lessonTitle = lessonTitle;
        this.completionScore = completionScore;
        this.quizScore = quizScore;
        this.overallScore = overallScore;
        this.weak = weak;
        this.recommendedContents = recommendedContents;
    }

    public long getLessonId() { return lessonId; }
    public String getLessonTitle() { return lessonTitle; }
    public double getCompletionScore() { return completionScore; }
    public double getQuizScore() { return quizScore; }
    public double getOverallScore() { return overallScore; }
    public boolean isWeak() { return weak; }
    public List<ContentItem> getRecommendedContents() { return recommendedContents; }
    public String getOverallPct() { return String.format("%.0f%%", overallScore * 100); }
    public String getWeakLabel() { return weak ? "Débil" : "OK"; }
}
