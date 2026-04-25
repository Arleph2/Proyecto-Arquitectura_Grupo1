package org.eclipse.jakarta.controller;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jakarta.dto.analysis.AnalysisResultDto;
import org.eclipse.jakarta.dto.analysis.LessonAnalysisDto;
import org.eclipse.jakarta.service.RecommendationService;

@Path("/analysis")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class AnalysisController {

    @EJB private RecommendationService recommendationService;

    @POST
    @Path("/enrollments/{enrollmentId}")
    public Response analyze(@PathParam("enrollmentId") Long enrollmentId) {
        AnalysisResultDto result = recommendationService.analyze(enrollmentId);
        return Response.ok(result).build();
    }

    @POST
    @Path("/enrollments/{enrollmentId}/lessons/{lessonId}")
    public Response analyzeLesson(@PathParam("enrollmentId") Long enrollmentId,
                                   @PathParam("lessonId") Long lessonId) {
        LessonAnalysisDto result = recommendationService.analyzeLesson(enrollmentId, lessonId);
        return Response.ok(result).build();
    }
}
