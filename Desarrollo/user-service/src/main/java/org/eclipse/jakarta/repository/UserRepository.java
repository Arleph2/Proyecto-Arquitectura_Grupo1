package org.eclipse.jakarta.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.jakarta.entity.user.User;

@Stateless
public class UserRepository {

    @PersistenceContext(unitName = "UserPU")
    private EntityManager em;

    // Returns a JPA proxy without issuing a SELECT — safe for FK references when persisting related entities.
    public User getReference(Long userId) {
        return em.getReference(User.class, userId);
    }
}
