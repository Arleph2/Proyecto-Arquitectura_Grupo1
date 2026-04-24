package org.eclipse.jakarta.entity.content;

import jakarta.persistence.*;

@Entity
@Table(name = "file_contents")
public class FileContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id", nullable = false, unique = true)
    private Content content;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_type")
    private String fileType;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Content getContent() { return content; }
    public void setContent(Content content) { this.content = content; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
}
