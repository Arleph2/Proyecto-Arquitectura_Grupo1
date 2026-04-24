package org.eclipse.jakarta.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jakarta.dto.simulation.SimulationResultDto;
import org.eclipse.jakarta.service.CourseSimulationService;

@Path("/simulations")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulationController {

    @Inject
    private CourseSimulationService simulationService;

    @POST
    @Path("/enrollments/{enrollmentId}")
    public Response simulate(@PathParam("enrollmentId") Long enrollmentId) {
        SimulationResultDto result = simulationService.simulate(enrollmentId);
        return Response.ok(result).build();
    }
}
