package org.eclipse.jakarta.repository;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.jakarta.dto.user.EnrollmentDto;
import org.eclipse.jakarta.dto.user.LessonProgressDto;
import org.eclipse.jakarta.dto.user.QuizAttemptDto;
import java.util.List;

@RegisterRestClient(configKey = "user-service")
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UserServiceClient {

    @GET
    @Path("/enrollments/{enrollmentId}")
    EnrollmentDto getEnrollment(@PathParam("enrollmentId") Long enrollmentId);

    @GET
    @Path("/lesson-progress/user/{userId}")
    List<LessonProgressDto> getLessonProgressByUser(@PathParam("userId") Long userId);

    @GET
    @Path("/quiz-attempts/user/{userId}")
    List<QuizAttemptDto> getQuizAttemptsByUser(@PathParam("userId") Long userId);
}
