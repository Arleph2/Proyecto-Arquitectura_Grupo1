package org.eclipse.jakarta.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.jakarta.entity.user.Enrollment;
import java.util.Optional;

@Stateless
public class EnrollmentRepository {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    public Optional<Enrollment> findById(Long id) {
        return Optional.ofNullable(em.find(Enrollment.class, id));
    }

    public void updateProgress(Long enrollmentId, double progress) {
        em.createQuery("UPDATE Enrollment e SET e.progress = :progress WHERE e.id = :id")
                .setParameter("progress", progress)
                .setParameter("id", enrollmentId)
                .executeUpdate();
    }
}
