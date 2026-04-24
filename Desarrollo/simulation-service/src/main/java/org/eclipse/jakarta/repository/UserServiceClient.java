package org.eclipse.jakarta.repository;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.jakarta.dto.user.*;

@RegisterRestClient(configKey = "user-service")
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UserServiceClient {

    @GET
    @Path("/enrollments/{enrollmentId}")
    EnrollmentDto getEnrollment(@PathParam("enrollmentId") Long enrollmentId);

    @POST
    @Path("/lesson-progress/start")
    LessonProgressDto startLesson(StartLessonRequestDto request);

    @PUT
    @Path("/lesson-progress/{progressId}/complete")
    void completeLesson(@PathParam("progressId") Long progressId, CompleteLessonRequestDto request);

    @PUT
    @Path("/enrollments/{enrollmentId}/progress")
    void updateProgress(@PathParam("enrollmentId") Long enrollmentId, UpdateProgressRequestDto request);

    @POST
    @Path("/quiz-attempts")
    QuizAttemptResponseDto recordQuizAttempt(QuizAttemptRequestDto request);
}
