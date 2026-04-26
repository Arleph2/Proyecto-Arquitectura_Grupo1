package org.eclipse.jakarta.dto;

import java.io.Serializable;

public class VideoItem implements Serializable {
    private long id;
    private String url;
    private int duration;
    private String provider;

    public VideoItem(long id, String url, int duration, String provider) {
        this.id = id;
        this.url = url;
        this.duration = duration;
        this.provider = provider;
    }

    public long getId() { return id; }
    public String getUrl() { return url; }
    public int getDuration() { return duration; }
    public String getProvider() { return provider; }
    public String getFormattedDuration() {
        return (duration / 60) + "m " + (duration % 60) + "s";
    }
}
