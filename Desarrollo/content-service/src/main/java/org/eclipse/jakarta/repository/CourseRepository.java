package org.eclipse.jakarta.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.jakarta.entity.content.Course;
import java.util.Optional;

@Stateless
public class CourseRepository {

    @PersistenceContext(unitName = "ContentPU")
    private EntityManager em;

    public Optional<Course> findById(Long id) {
        return Optional.ofNullable(em.find(Course.class, id));
    }
}
