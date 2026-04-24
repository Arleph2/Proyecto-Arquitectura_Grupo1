package org.eclipse.jakarta.entity.content;

import jakarta.persistence.*;

@Entity
@Table(name = "video_contents")
public class VideoContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id", nullable = false, unique = true)
    private Content content;

    @Column(nullable = false)
    private String url;

    private Integer duration;

    private String provider;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Content getContent() { return content; }
    public void setContent(Content content) { this.content = content; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
}
