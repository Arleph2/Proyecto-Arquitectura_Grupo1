package org.eclipse.jakarta.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.jakarta.client.ServiceClient;
import org.eclipse.jakarta.dto.*;

import java.io.Serializable;
import java.util.*;

@Named
@SessionScoped
public class CourseBean implements Serializable {

    @Inject private ServiceClient client;

    private long enrollmentId = 1;
    private long userId;
    private long courseId;

    private List<ModuleItem> modules = new ArrayList<>();
    private Map<Long, List<LessonItem>> lessonsByModule = new LinkedHashMap<>();
    private Set<Long> completedLessons = new HashSet<>();

    private long selectedLessonId = -1;
    private long selectedModuleId = -1;

    private Set<Long> weakLessonIds    = new HashSet<>();
    private Map<Long, Long> progressIds = new LinkedHashMap<>(); // lessonId → progressId

    @PostConstruct
    public void init() {
        loadEnrollment();
    }

    public void loadEnrollment() {
        EnrollmentItem enr = client.getEnrollment(enrollmentId);
        if (enr == null) return;
        userId   = enr.getUserId();
        courseId = enr.getCourseId();

        modules = client.getModules(courseId);
        lessonsByModule.clear();
        for (ModuleItem m : modules) {
            lessonsByModule.put(m.getId(), client.getLessons(m.getId()));
        }
        refreshProgress();
    }

    public void refreshProgress() {
        completedLessons.clear();
        progressIds.clear();
        for (ProgressItem p : client.getProgress(userId)) {
            progressIds.put(p.getLessonId(), p.getId());
            if ("COMPLETED".equalsIgnoreCase(p.getStatus())) {
                completedLessons.add(p.getLessonId());
            }
        }
        weakLessonIds.clear();
        for (RecommendationItem r : client.getRecommendations(userId)) {
            weakLessonIds.add(r.getLessonId());
        }
    }

    public long getProgressId(long lessonId) {
        return progressIds.getOrDefault(lessonId, -1L);
    }

    public boolean isWeakLesson(long lessonId) {
        return weakLessonIds.contains(lessonId);
    }

    /** Called after view params are applied to derive selectedModuleId. */
    public void initSelection() {
        if (selectedLessonId <= 0) {
            // default to first lesson
            if (!modules.isEmpty()) {
                ModuleItem first = modules.get(0);
                selectedModuleId = first.getId();
                List<LessonItem> ls = lessonsByModule.get(selectedModuleId);
                if (ls != null && !ls.isEmpty()) selectedLessonId = ls.get(0).getId();
            }
        } else {
            selectedModuleId = getModuleIdForLesson(selectedLessonId);
        }
    }

    public long getModuleIdForLesson(long lessonId) {
        for (Map.Entry<Long, List<LessonItem>> e : lessonsByModule.entrySet()) {
            for (LessonItem l : e.getValue()) {
                if (l.getId() == lessonId) return e.getKey();
            }
        }
        return -1;
    }

    public List<LessonItem> getLessons(long moduleId) {
        return lessonsByModule.getOrDefault(moduleId, List.of());
    }

    public boolean isCompleted(long lessonId) { return completedLessons.contains(lessonId); }

    public int getCompletedCount() { return completedLessons.size(); }

    public int getTotalLessons() {
        return lessonsByModule.values().stream().mapToInt(List::size).sum();
    }

    public int getProgressPct() {
        int total = getTotalLessons();
        return total == 0 ? 0 : (int) Math.round(completedLessons.size() * 100.0 / total);
    }

    public LessonItem getNextLesson() {
        boolean found = false;
        for (ModuleItem m : modules)
            for (LessonItem l : getLessons(m.getId())) {
                if (found) return l;
                if (l.getId() == selectedLessonId) found = true;
            }
        return null;
    }

    public LessonItem getPrevLesson() {
        LessonItem prev = null;
        for (ModuleItem m : modules)
            for (LessonItem l : getLessons(m.getId())) {
                if (l.getId() == selectedLessonId) return prev;
                prev = l;
            }
        return null;
    }

    public String getFirstLessonId() {
        if (!modules.isEmpty()) {
            List<LessonItem> ls = lessonsByModule.get(modules.get(0).getId());
            if (ls != null && !ls.isEmpty()) return String.valueOf(ls.get(0).getId());
        }
        return "1";
    }

    // getters / setters
    public long getEnrollmentId()           { return enrollmentId; }
    public void setEnrollmentId(long v)     { this.enrollmentId = v; }
    public long getUserId()                 { return userId; }
    public long getCourseId()               { return courseId; }
    public List<ModuleItem> getModules()    { return modules; }
    public long getSelectedLessonId()       { return selectedLessonId; }
    public void setSelectedLessonId(long v) { this.selectedLessonId = v; }
    public long getSelectedModuleId()       { return selectedModuleId; }
    public void setSelectedModuleId(long v) { this.selectedModuleId = v; }
}
