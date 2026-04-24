package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.content.ArticleContent;

public class ArticleContentDto {
    private Long id;
    private Long contentId;
    private String body;

    public static ArticleContentDto from(ArticleContent a) {
        ArticleContentDto dto = new ArticleContentDto();
        dto.id = a.getId();
        dto.contentId = a.getContent().getId();
        dto.body = a.getBody();
        return dto;
    }

    public Long getId() { return id; }
    public Long getContentId() { return contentId; }
    public String getBody() { return body; }
}
