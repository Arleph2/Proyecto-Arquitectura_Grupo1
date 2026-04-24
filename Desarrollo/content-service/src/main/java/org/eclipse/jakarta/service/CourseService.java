package org.eclipse.jakarta.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.eclipse.jakarta.repository.CourseRepository;
import org.eclipse.jakarta.repository.ModuleRepository;
import org.eclipse.jakarta.dto.CourseDto;
import org.eclipse.jakarta.dto.ModuleDto;
import org.eclipse.jakarta.entity.content.Module;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class CourseService {

    @EJB private CourseRepository courseRepository;
    @EJB private ModuleRepository moduleRepository;

    public Optional<CourseDto> findCourse(Long courseId) {
        return courseRepository.findById(courseId).map(CourseDto::from);
    }

    public List<ModuleDto> findModulesOrdered(Long courseId) {
        List<Module> modules = moduleRepository.findByCourseIdOrdered(courseId);
        return modules.stream().map(ModuleDto::from).collect(Collectors.toList());
    }
}
