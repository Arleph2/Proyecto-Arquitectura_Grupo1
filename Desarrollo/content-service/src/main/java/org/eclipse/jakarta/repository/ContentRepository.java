package org.eclipse.jakarta.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.jakarta.entity.content.ArticleContent;
import org.eclipse.jakarta.entity.content.Content;
import org.eclipse.jakarta.entity.content.ContentPurpose;
import org.eclipse.jakarta.entity.content.FileContent;
import org.eclipse.jakarta.entity.content.VideoContent;
import java.util.List;
import java.util.Optional;

@Stateless
public class ContentRepository {

    @PersistenceContext(unitName = "ContentPU")
    private EntityManager em;

    public List<Content> findByLessonIdOrdered(Long lessonId) {
        return em.createQuery(
                "SELECT c FROM Content c WHERE c.lesson.id = :lessonId AND c.purpose = :purpose ORDER BY c.position",
                Content.class)
                .setParameter("lessonId", lessonId)
                .setParameter("purpose", ContentPurpose.LESSON)
                .getResultList();
    }

    public List<Content> findReinforcementByLessonId(Long lessonId) {
        return em.createQuery(
                "SELECT c FROM Content c WHERE c.lesson.id = :lessonId AND c.purpose = :purpose ORDER BY c.position",
                Content.class)
                .setParameter("lessonId", lessonId)
                .setParameter("purpose", ContentPurpose.REINFORCEMENT)
                .getResultList();
    }

    public Optional<VideoContent> findVideoByContentId(Long contentId) {
        return em.createQuery(
                "SELECT v FROM VideoContent v WHERE v.content.id = :contentId",
                VideoContent.class)
                .setParameter("contentId", contentId)
                .getResultStream().findFirst();
    }

    public Optional<ArticleContent> findArticleByContentId(Long contentId) {
        return em.createQuery(
                "SELECT a FROM ArticleContent a WHERE a.content.id = :contentId",
                ArticleContent.class)
                .setParameter("contentId", contentId)
                .getResultStream().findFirst();
    }

    public Optional<FileContent> findFileByContentId(Long contentId) {
        return em.createQuery(
                "SELECT f FROM FileContent f WHERE f.content.id = :contentId",
                FileContent.class)
                .setParameter("contentId", contentId)
                .getResultStream().findFirst();
    }

}
