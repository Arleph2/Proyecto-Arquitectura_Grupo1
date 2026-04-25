package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.Recommendation;
import java.time.LocalDateTime;

public class RecommendationDto {
    private Long id;
    private Long userId;
    private Long lessonId;
    private Double score;
    private String reason;
    private LocalDateTime createdAt;

    public static RecommendationDto from(Recommendation r) {
        RecommendationDto dto = new RecommendationDto();
        dto.id = r.getId();
        dto.userId = r.getUserId();
        dto.lessonId = r.getLessonId();
        dto.score = r.getScore();
        dto.reason = r.getReason();
        dto.createdAt = r.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getLessonId() { return lessonId; }
    public Double getScore() { return score; }
    public String getReason() { return reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
