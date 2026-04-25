package org.eclipse.jakarta.dto;

import java.io.Serializable;

public class ModuleItem implements Serializable {
    private long id;
    private String title;

    public ModuleItem(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
}
