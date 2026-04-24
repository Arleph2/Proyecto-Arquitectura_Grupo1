package org.eclipse.jakarta.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.jakarta.entity.user.QuestionAttempt;
import org.eclipse.jakarta.entity.user.QuizAttempt;
import java.util.List;

@Stateless
public class QuizAttemptRepository {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public long countByUserIdAndQuizId(Long userId, Long quizId) {
        return em.createQuery(
                "SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.quizId = :quizId",
                Long.class)
                .setParameter("userId", userId)
                .setParameter("quizId", quizId)
                .getSingleResult();
    }

    public QuizAttempt save(QuizAttempt attempt) {
        em.persist(attempt);
        return attempt;
    }

    public void saveQuestionAttempts(List<QuestionAttempt> attempts) {
        attempts.forEach(em::persist);
    }

}
