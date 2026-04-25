package org.eclipse.jakarta.dto.analysis;

import java.util.List;

public class LessonAnalysisDto {
    private Long lessonId;
    private String lessonTitle;
    private Double completionScore;
    private Double quizScore;
    private Double overallScore;
    private boolean weak;
    private List<ContentSummaryDto> recommendedContents;

    public LessonAnalysisDto() {}

    public LessonAnalysisDto(Long lessonId, String lessonTitle, Double completionScore,
                              Double quizScore, Double overallScore, boolean weak,
                              List<ContentSummaryDto> recommendedContents) {
        this.lessonId = lessonId;
        this.lessonTitle = lessonTitle;
        this.completionScore = completionScore;
        this.quizScore = quizScore;
        this.overallScore = overallScore;
        this.weak = weak;
        this.recommendedContents = recommendedContents;
    }

    public Long getLessonId() { return lessonId; }
    public String getLessonTitle() { return lessonTitle; }
    public Double getCompletionScore() { return completionScore; }
    public Double getQuizScore() { return quizScore; }
    public Double getOverallScore() { return overallScore; }
    public boolean isWeak() { return weak; }
    public List<ContentSummaryDto> getRecommendedContents() { return recommendedContents; }
}
