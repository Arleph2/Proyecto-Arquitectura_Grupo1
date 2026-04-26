package org.eclipse.jakarta.dto;

import java.io.Serializable;

public class ArticleItem implements Serializable {
    private long id;
    private String body;

    public ArticleItem(long id, String body) {
        this.id = id;
        this.body = body;
    }

    public long getId() { return id; }
    public String getBody() { return body; }
}
