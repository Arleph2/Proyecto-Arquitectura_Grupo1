package org.eclipse.jakarta.dto.user;

public class UpdateProgressRequestDto {
    private Double progress;

    public UpdateProgressRequestDto() {}

    public UpdateProgressRequestDto(Double progress) {
        this.progress = progress;
    }

    public Double getProgress() { return progress; }
    public void setProgress(Double progress) { this.progress = progress; }
}
