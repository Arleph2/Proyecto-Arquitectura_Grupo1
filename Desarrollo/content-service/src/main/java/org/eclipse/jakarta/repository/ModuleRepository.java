package org.eclipse.jakarta.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.jakarta.entity.content.Module;
import java.util.List;

@Stateless
public class ModuleRepository {

    @PersistenceContext(unitName = "ContentPU")
    private EntityManager em;

    public List<Module> findByCourseIdOrdered(Long courseId) {
        return em.createQuery(
                "SELECT m FROM Module m WHERE m.course.id = :courseId ORDER BY m.position",
                Module.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }
}
