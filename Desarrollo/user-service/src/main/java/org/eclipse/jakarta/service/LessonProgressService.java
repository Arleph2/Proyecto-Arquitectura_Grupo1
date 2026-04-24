package org.eclipse.jakarta.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.eclipse.jakarta.repository.LessonProgressRepository;
import org.eclipse.jakarta.repository.UserRepository;
import org.eclipse.jakarta.dto.CompleteLessonDto;
import org.eclipse.jakarta.dto.LessonProgressDto;
import org.eclipse.jakarta.dto.StartLessonDto;
import org.eclipse.jakarta.entity.user.LessonProgress;
import org.eclipse.jakarta.entity.user.LessonStatus;
import org.eclipse.jakarta.exception.ResourceNotFoundException;
import java.time.LocalDateTime;

@Stateless
public class LessonProgressService {

    @EJB private LessonProgressRepository lessonProgressRepository;
    @EJB private UserRepository userRepository;

    public LessonProgressDto startLesson(StartLessonDto request) {
        LessonProgress lp = lessonProgressRepository
                .findByUserIdAndLessonId(request.getUserId(), request.getLessonId())
                .map(existing -> {
                    existing.setStatus(LessonStatus.IN_PROGRESS);
                    existing.setProgressPercent(0.0);
                    existing.setCompletedAt(null);
                    return lessonProgressRepository.save(existing);
                })
                .orElseGet(() -> {
                    LessonProgress fresh = new LessonProgress();
                    fresh.setUser(userRepository.getReference(request.getUserId()));
                    fresh.setLessonId(request.getLessonId());
                    fresh.setStatus(LessonStatus.IN_PROGRESS);
                    fresh.setProgressPercent(0.0);
                    fresh.setTimeSpent(0);
                    return lessonProgressRepository.save(fresh);
                });

        return LessonProgressDto.from(lp);
    }

    public void completeLesson(Long progressId, CompleteLessonDto request) {
        LessonProgress lp = lessonProgressRepository.findById(progressId)
                .orElseThrow(() -> new ResourceNotFoundException("LessonProgress", progressId));
        lp.setStatus(LessonStatus.COMPLETED);
        lp.setProgressPercent(100.0);
        lp.setTimeSpent(request.getTimeSpent());
        lp.setCompletedAt(LocalDateTime.now());
        lessonProgressRepository.save(lp);
    }
}
