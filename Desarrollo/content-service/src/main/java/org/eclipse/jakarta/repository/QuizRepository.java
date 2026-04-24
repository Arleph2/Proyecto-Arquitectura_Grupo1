package org.eclipse.jakarta.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.jakarta.entity.content.Answer;
import org.eclipse.jakarta.entity.content.Quiz;
import java.util.List;
import java.util.Optional;

@Stateless
public class QuizRepository {

    @PersistenceContext(unitName = "ContentPU")
    private EntityManager em;

    public Optional<Quiz> findByContentIdWithQuestions(Long contentId) {
        List<Quiz> results = em.createQuery(
                "SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.content.id = :contentId",
                Quiz.class)
                .setParameter("contentId", contentId)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<Answer> findCorrectAnswerByQuestionId(Long questionId) {
        return em.createQuery(
                "SELECT a FROM Answer a WHERE a.question.id = :questionId AND a.correct = true",
                Answer.class)
                .setParameter("questionId", questionId)
                .getResultStream().findFirst();
    }
}
