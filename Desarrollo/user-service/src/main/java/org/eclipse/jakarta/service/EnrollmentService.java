package org.eclipse.jakarta.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.eclipse.jakarta.repository.EnrollmentRepository;
import org.eclipse.jakarta.dto.EnrollmentDto;
import org.eclipse.jakarta.dto.UpdateProgressDto;
import org.eclipse.jakarta.exception.ResourceNotFoundException;
import java.util.Optional;

@Stateless
public class EnrollmentService {

    @EJB private EnrollmentRepository enrollmentRepository;

    public Optional<EnrollmentDto> findById(Long id) {
        return enrollmentRepository.findById(id).map(EnrollmentDto::from);
    }

    public void updateProgress(Long enrollmentId, UpdateProgressDto body) {
        enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));
        enrollmentRepository.updateProgress(enrollmentId, body.getProgress());
    }
}
