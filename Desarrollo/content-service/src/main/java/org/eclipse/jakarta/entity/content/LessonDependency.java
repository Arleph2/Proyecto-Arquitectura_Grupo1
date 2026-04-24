package org.eclipse.jakarta.entity.content;

import jakarta.persistence.*;

@Entity
@Table(name = "lesson_dependencies")
public class LessonDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "depends_on_lesson_id", nullable = false)
    private Lesson dependsOnLesson;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }

    public Lesson getDependsOnLesson() { return dependsOnLesson; }
    public void setDependsOnLesson(Lesson dependsOnLesson) { this.dependsOnLesson = dependsOnLesson; }
}
