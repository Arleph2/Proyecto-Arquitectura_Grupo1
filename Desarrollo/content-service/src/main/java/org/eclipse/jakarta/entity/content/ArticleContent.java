package org.eclipse.jakarta.entity.content;

import jakarta.persistence.*;

@Entity
@Table(name = "article_contents")
public class ArticleContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id", nullable = false, unique = true)
    private Content content;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Content getContent() { return content; }
    public void setContent(Content content) { this.content = content; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
