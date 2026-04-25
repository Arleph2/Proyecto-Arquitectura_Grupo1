package org.eclipse.jakarta.repository;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "recommendation-service")
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public interface RecommendationServiceClient {

    @POST
    @Path("/analysis/enrollments/{enrollmentId}/lessons/{lessonId}")
    void analyzeLesson(@PathParam("enrollmentId") Long enrollmentId,
                       @PathParam("lessonId") Long lessonId);
}
