package org.eclipse.jakarta.controller;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.jakarta.dto.CourseDto;
import org.eclipse.jakarta.dto.ModuleDto;
import org.eclipse.jakarta.service.CourseService;
import java.util.List;

@Path("/courses")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class CourseController {

    @EJB private CourseService courseService;

    @GET
    @Path("/{courseId}")
    public Response getCourse(@PathParam("courseId") Long courseId) {
        return courseService.findCourse(courseId)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/{courseId}/modules")
    public Response getModules(@PathParam("courseId") Long courseId) {
        List<ModuleDto> modules = courseService.findModulesOrdered(courseId);
        return Response.ok(modules).build();
    }
}
