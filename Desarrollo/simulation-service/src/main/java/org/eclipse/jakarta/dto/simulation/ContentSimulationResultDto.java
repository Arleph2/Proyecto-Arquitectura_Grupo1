package org.eclipse.jakarta.dto.simulation;

public class ContentSimulationResultDto {
    private Long contentId;
    private String type;
    private int timeSpent;
    private Long quizId;
    private Double score;
    private Double maxScore;

    private ContentSimulationResultDto() {}

    public static ContentSimulationResultDto video(Long contentId, int timeSpent) {
        ContentSimulationResultDto dto = new ContentSimulationResultDto();
        dto.contentId = contentId; dto.type = "VIDEO"; dto.timeSpent = timeSpent;
        return dto;
    }

    public static ContentSimulationResultDto article(Long contentId, int timeSpent) {
        ContentSimulationResultDto dto = new ContentSimulationResultDto();
        dto.contentId = contentId; dto.type = "ARTICLE"; dto.timeSpent = timeSpent;
        return dto;
    }

    public static ContentSimulationResultDto file(Long contentId, int timeSpent) {
        ContentSimulationResultDto dto = new ContentSimulationResultDto();
        dto.contentId = contentId; dto.type = "FILE"; dto.timeSpent = timeSpent;
        return dto;
    }

    public static ContentSimulationResultDto quiz(Long contentId, Long quizId,
                                                   double score, double maxScore, int timeSpent) {
        ContentSimulationResultDto dto = new ContentSimulationResultDto();
        dto.contentId = contentId; dto.type = "QUIZ"; dto.quizId = quizId;
        dto.score = score; dto.maxScore = maxScore; dto.timeSpent = timeSpent;
        return dto;
    }

    public Long getContentId() { return contentId; }
    public String getType() { return type; }
    public int getTimeSpent() { return timeSpent; }
    public Long getQuizId() { return quizId; }
    public Double getScore() { return score; }
    public Double getMaxScore() { return maxScore; }
}
