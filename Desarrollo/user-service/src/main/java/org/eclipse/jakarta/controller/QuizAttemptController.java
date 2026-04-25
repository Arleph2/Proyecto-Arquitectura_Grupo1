package org.eclipse.jakarta.controller;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jakarta.dto.QuizAttemptRequestDto;
import org.eclipse.jakarta.dto.QuizAttemptResponseDto;
import org.eclipse.jakarta.service.QuizAttemptService;
import java.util.List;

@Path("/quiz-attempts")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuizAttemptController {

    @EJB private QuizAttemptService quizAttemptService;

    @GET
    @Path("/user/{userId}")
    public Response findByUserId(@PathParam("userId") Long userId) {
        List<QuizAttemptResponseDto> result = quizAttemptService.findAllByUserId(userId);
        return Response.ok(result).build();
    }

    @POST
    public Response recordAttempt(QuizAttemptRequestDto request) {
        QuizAttemptResponseDto result = quizAttemptService.recordAttempt(request);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }
}
