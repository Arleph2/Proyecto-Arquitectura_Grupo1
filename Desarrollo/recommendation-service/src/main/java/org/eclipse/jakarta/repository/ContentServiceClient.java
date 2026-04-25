package org.eclipse.jakarta.repository;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.jakarta.dto.content.ContentDto;
import org.eclipse.jakarta.dto.content.LessonDto;
import org.eclipse.jakarta.dto.content.ModuleDto;
import org.eclipse.jakarta.dto.content.QuizDto;
import java.util.List;

@RegisterRestClient(configKey = "content-service")
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ContentServiceClient {

    @GET
    @Path("/lessons/{lessonId}")
    LessonDto getLesson(@PathParam("lessonId") Long lessonId);

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
    @Path("/contents/{contentId}/quiz")
    QuizDto getQuiz(@PathParam("contentId") Long contentId);

    @GET
    @Path("/lessons/{lessonId}/reinforcement")
    List<ContentDto> getReinforcementContents(@PathParam("lessonId") Long lessonId);
}
