package org.eclipse.jakarta.entity.content;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type;

    @Column(nullable = false)
    private Integer position;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }

    public ContentType getType() { return type; }
    public void setType(ContentType type) { this.type = type; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
