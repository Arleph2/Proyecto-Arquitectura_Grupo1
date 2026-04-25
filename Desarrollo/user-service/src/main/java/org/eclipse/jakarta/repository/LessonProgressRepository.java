package org.eclipse.jakarta.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.jakarta.entity.user.LessonProgress;
import java.util.List;
import java.util.Optional;

@Stateless
public class LessonProgressRepository {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId) {
        return em.createQuery(
                "SELECT lp FROM LessonProgress lp WHERE lp.user.id = :userId AND lp.lessonId = :lessonId",
                LessonProgress.class)
                .setParameter("userId", userId)
                .setParameter("lessonId", lessonId)
                .getResultStream().findFirst();
    }

    public Optional<LessonProgress> findById(Long id) {
        return Optional.ofNullable(em.find(LessonProgress.class, id));
    }

    public List<LessonProgress> findByUserId(Long userId) {
        return em.createQuery(
                "SELECT lp FROM LessonProgress lp WHERE lp.user.id = :userId",
                LessonProgress.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public LessonProgress save(LessonProgress lp) {
        if (lp.getId() == null) {
            em.persist(lp);
            return lp;
        }
        return em.merge(lp);
    }

}
