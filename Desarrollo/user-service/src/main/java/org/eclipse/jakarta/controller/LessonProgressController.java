package org.eclipse.jakarta.controller;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jakarta.dto.CompleteLessonDto;
import org.eclipse.jakarta.dto.LessonProgressDto;
import org.eclipse.jakarta.dto.StartLessonDto;
import org.eclipse.jakarta.service.LessonProgressService;

@Path("/lesson-progress")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LessonProgressController {

    @EJB private LessonProgressService lessonProgressService;

    @POST
    @Path("/start")
    public Response startLesson(StartLessonDto request) {
        LessonProgressDto result = lessonProgressService.startLesson(request);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{progressId}/complete")
    public Response completeLesson(@PathParam("progressId") Long progressId,
                                    CompleteLessonDto request) {
        lessonProgressService.completeLesson(progressId, request);
        return Response.noContent().build();
    }
}
