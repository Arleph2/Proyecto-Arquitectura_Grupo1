package org.eclipse.jakarta.controller;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jakarta.dto.ContentDto;
import org.eclipse.jakarta.dto.LessonDto;
import org.eclipse.jakarta.service.LessonService;
import java.util.List;

@Path("/")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class LessonController {

    @EJB private LessonService lessonService;

    @GET
    @Path("/lessons/{lessonId}")
    public Response getLesson(@PathParam("lessonId") Long lessonId) {
        return lessonService.findById(lessonId)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/modules/{moduleId}/lessons")
    public Response getLessons(@PathParam("moduleId") Long moduleId) {
        List<LessonDto> lessons = lessonService.findLessonsOrdered(moduleId);
        return Response.ok(lessons).build();
    }

    @GET
    @Path("/lessons/{lessonId}/contents")
    public Response getContents(@PathParam("lessonId") Long lessonId) {
        List<ContentDto> contents = lessonService.findContentsOrdered(lessonId);
        return Response.ok(contents).build();
    }

    @GET
    @Path("/lessons/{lessonId}/reinforcement")
    public Response getReinforcementContents(@PathParam("lessonId") Long lessonId) {
        List<ContentDto> contents = lessonService.findReinforcementContents(lessonId);
        return Response.ok(contents).build();
    }

    @GET
    @Path("/contents/{contentId}/video")
    public Response getVideo(@PathParam("contentId") Long contentId) {
        return lessonService.findVideo(contentId)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/contents/{contentId}/article")
    public Response getArticle(@PathParam("contentId") Long contentId) {
        return lessonService.findArticle(contentId)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/contents/{contentId}/file")
    public Response getFile(@PathParam("contentId") Long contentId) {
        return lessonService.findFile(contentId)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
