package org.eclipse.jakarta.controller;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jakarta.service.QuizService;

@Path("/")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class QuizController {

    @EJB private QuizService quizService;

    @GET
    @Path("/contents/{contentId}/quiz")
    public Response getQuiz(@PathParam("contentId") Long contentId) {
        return quizService.findQuizByContentId(contentId)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/questions/{questionId}/correct-answer-id")
    public Response getCorrectAnswer(@PathParam("questionId") Long questionId) {
        return quizService.findCorrectAnswer(questionId)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
