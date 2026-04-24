package org.eclipse.jakarta.controller;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jakarta.dto.UpdateProgressDto;
import org.eclipse.jakarta.service.EnrollmentService;

@Path("/enrollments")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnrollmentController {

    @EJB private EnrollmentService enrollmentService;

    @GET
    @Path("/{enrollmentId}")
    public Response getEnrollment(@PathParam("enrollmentId") Long enrollmentId) {
        return enrollmentService.findById(enrollmentId)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{enrollmentId}/progress")
    public Response updateProgress(@PathParam("enrollmentId") Long enrollmentId,
                                    UpdateProgressDto body) {
        enrollmentService.updateProgress(enrollmentId, body);
        return Response.noContent().build();
    }
}
