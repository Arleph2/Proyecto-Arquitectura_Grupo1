package org.eclipse.jakarta.repository;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.jakarta.dto.content.*;
import java.util.List;

@RegisterRestClient(configKey = "content-service")
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ContentServiceClient {

    @GET
    @Path("/courses/{courseId}")
    CourseDto getCourse(@PathParam("courseId") Long courseId);

    @GET
    @Path("/courses/{courseId}/modules")
    List<ModuleDto> getModules(@PathParam("courseId") Long courseId);

    @GET
    @Path("/modules/{moduleId}/lessons")
    List<LessonDto> getLessons(@PathParam("moduleId") Long moduleId);

    @GET
    @Path("/lessons/{lessonId}/contents")
    List<ContentDto> getContents(@PathParam("lessonId") Long lessonId);

    @GET
    @Path("/contents/{contentId}/video")
    VideoContentDto getVideo(@PathParam("contentId") Long contentId);

    @GET
    @Path("/contents/{contentId}/article")
    ArticleContentDto getArticle(@PathParam("contentId") Long contentId);

    @GET
    @Path("/contents/{contentId}/quiz")
    QuizDto getQuiz(@PathParam("contentId") Long contentId);

    @GET
    @Path("/questions/{questionId}/correct-answer-id")
    CorrectAnswerDto getCorrectAnswer(@PathParam("questionId") Long questionId);
}
