package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.content.VideoContent;

public class VideoContentDto {
    private Long id;
    private Long contentId;
    private String url;
    private Integer duration;
    private String provider;

    public static VideoContentDto from(VideoContent v) {
        VideoContentDto dto = new VideoContentDto();
        dto.id = v.getId();
        dto.contentId = v.getContent().getId();
        dto.url = v.getUrl();
        dto.duration = v.getDuration();
        dto.provider = v.getProvider();
        return dto;
    }

    public Long getId() { return id; }
    public Long getContentId() { return contentId; }
    public String getUrl() { return url; }
    public Integer getDuration() { return duration; }
    public String getProvider() { return provider; }
}
