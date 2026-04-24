package org.eclipse.jakarta.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.jakarta.entity.content.Lesson;
import java.util.List;

@Stateless
public class LessonRepository {

    @PersistenceContext(unitName = "ContentPU")
    private EntityManager em;

    public List<Lesson> findByModuleIdOrdered(Long moduleId) {
        return em.createQuery(
                "SELECT l FROM Lesson l WHERE l.module.id = :moduleId ORDER BY l.position",
                Lesson.class)
                .setParameter("moduleId", moduleId)
                .getResultList();
    }
}
