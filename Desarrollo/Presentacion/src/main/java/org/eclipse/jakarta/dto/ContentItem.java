package org.eclipse.jakarta.dto;

import java.io.Serializable;

public class ContentItem implements Serializable {
    private long id;
    private String type;
    private int position;

    public ContentItem(long id, String type, int position) {
        this.id = id;
        this.type = type;
        this.position = position;
    }

    public long getId() { return id; }
    public String getType() { return type; }
    public int getPosition() { return position; }
}
