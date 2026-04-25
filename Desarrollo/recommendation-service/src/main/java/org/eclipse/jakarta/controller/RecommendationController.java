package org.eclipse.jakarta.controller;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jakarta.dto.RecommendationDto;
import org.eclipse.jakarta.service.RecommendationService;
import java.util.List;

@Path("/recommendations")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class RecommendationController {

    @EJB private RecommendationService recommendationService;

    @GET
    @Path("/user/{userId}")
    public Response findByUserId(@PathParam("userId") Long userId) {
        List<RecommendationDto> result = recommendationService.findByUserId(userId);
        return Response.ok(result).build();
    }
}
