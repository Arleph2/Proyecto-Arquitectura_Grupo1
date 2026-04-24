package org.eclipse.jakarta.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.eclipse.jakarta.repository.ContentRepository;
import org.eclipse.jakarta.repository.LessonRepository;
import org.eclipse.jakarta.dto.*;
import org.eclipse.jakarta.entity.content.Content;
import org.eclipse.jakarta.entity.content.Lesson;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class LessonService {

    @EJB private LessonRepository lessonRepository;
    @EJB private ContentRepository contentRepository;

    public List<LessonDto> findLessonsOrdered(Long moduleId) {
        List<Lesson> lessons = lessonRepository.findByModuleIdOrdered(moduleId);
        return lessons.stream().map(LessonDto::from).collect(Collectors.toList());
    }

    public List<ContentDto> findContentsOrdered(Long lessonId) {
        List<Content> contents = contentRepository.findByLessonIdOrdered(lessonId);
        return contents.stream().map(ContentDto::from).collect(Collectors.toList());
    }

    public Optional<VideoContentDto> findVideo(Long contentId) {
        return contentRepository.findVideoByContentId(contentId).map(VideoContentDto::from);
    }

    public Optional<ArticleContentDto> findArticle(Long contentId) {
        return contentRepository.findArticleByContentId(contentId).map(ArticleContentDto::from);
    }
}
