package org.eclipse.jakarta.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.jakarta.entity.Recommendation;
import java.util.List;

@Stateless
public class RecommendationRepository {

    @PersistenceContext(unitName = "RecommendationPU")
    private EntityManager em;

    public void deleteByUserIdAndLessonId(Long userId, Long lessonId) {
        em.createQuery(
                "DELETE FROM Recommendation r WHERE r.userId = :userId AND r.lessonId = :lessonId")
                .setParameter("userId", userId)
                .setParameter("lessonId", lessonId)
                .executeUpdate();
    }

    public Recommendation save(Recommendation r) {
        em.persist(r);
        return r;
    }

    public List<Recommendation> findByUserId(Long userId) {
        return em.createQuery(
                "SELECT r FROM Recommendation r WHERE r.userId = :userId ORDER BY r.createdAt DESC",
                Recommendation.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
