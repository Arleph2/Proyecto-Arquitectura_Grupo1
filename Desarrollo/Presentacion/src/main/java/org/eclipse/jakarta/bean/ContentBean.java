package org.eclipse.jakarta.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.jakarta.client.ServiceClient;
import org.eclipse.jakarta.dto.ContentItem;
import org.eclipse.jakarta.dto.LessonItem;
import org.eclipse.jakarta.dto.ModuleItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class ContentBean implements Serializable {

    @Inject
    private ServiceClient client;

    private final long courseId = 1L;

    private List<ModuleItem>  modules      = new ArrayList<>();
    private Long              selectedModuleId;
    private String            selectedModuleTitle;

    private List<LessonItem>  lessons      = new ArrayList<>();
    private Long              selectedLessonId;
    private String            selectedLessonTitle;

    private List<ContentItem> contents              = new ArrayList<>();
    private List<ContentItem> reinforcementContents = new ArrayList<>();

    @PostConstruct
    public void init() {
        modules = client.getModules(courseId);
        if (modules.isEmpty()) {
            addWarn("No se pudieron cargar los módulos del curso.");
        }
    }

    public void onModuleSelect() {
        lessons               = new ArrayList<>();
        selectedLessonId      = null;
        selectedLessonTitle   = null;
        contents              = new ArrayList<>();
        reinforcementContents = new ArrayList<>();

        if (selectedModuleId == null) return;

        lessons = client.getLessons(selectedModuleId);
        modules.stream().filter(m -> m.getId() == selectedModuleId).findFirst()
               .ifPresent(m -> selectedModuleTitle = m.getTitle());
    }

    public void onLessonSelect() {
        contents              = new ArrayList<>();
        reinforcementContents = new ArrayList<>();

        if (selectedLessonId == null) return;

        contents              = client.getContents(selectedLessonId);
        reinforcementContents = client.getReinforcementContents(selectedLessonId);
        lessons.stream().filter(l -> l.getId() == selectedLessonId).findFirst()
               .ifPresent(l -> selectedLessonTitle = l.getTitle());
    }

    private void addWarn(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", msg));
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public long getCourseId() { return courseId; }

    public List<ModuleItem> getModules() { return modules; }

    public Long getSelectedModuleId() { return selectedModuleId; }
    public void setSelectedModuleId(Long selectedModuleId) { this.selectedModuleId = selectedModuleId; }
    public String getSelectedModuleTitle() { return selectedModuleTitle; }

    public List<LessonItem> getLessons() { return lessons; }

    public Long getSelectedLessonId() { return selectedLessonId; }
    public void setSelectedLessonId(Long selectedLessonId) { this.selectedLessonId = selectedLessonId; }
    public String getSelectedLessonTitle() { return selectedLessonTitle; }

    public List<ContentItem> getContents() { return contents; }
    public List<ContentItem> getReinforcementContents() { return reinforcementContents; }

    public boolean isLessonsVisible()      { return selectedModuleId != null && !lessons.isEmpty(); }
    public boolean isContentsVisible()     { return selectedLessonId != null; }
}
