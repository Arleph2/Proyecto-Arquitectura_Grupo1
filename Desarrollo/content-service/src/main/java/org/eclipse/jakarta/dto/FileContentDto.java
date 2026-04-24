package org.eclipse.jakarta.dto;

import org.eclipse.jakarta.entity.content.FileContent;

public class FileContentDto {
    private Long id;
    private Long contentId;
    private String fileUrl;
    private String fileType;

    public static FileContentDto from(FileContent f) {
        FileContentDto dto = new FileContentDto();
        dto.id = f.getId();
        dto.contentId = f.getContent().getId();
        dto.fileUrl = f.getFileUrl();
        dto.fileType = f.getFileType();
        return dto;
    }

    public Long getId() { return id; }
    public Long getContentId() { return contentId; }
    public String getFileUrl() { return fileUrl; }
    public String getFileType() { return fileType; }
}
