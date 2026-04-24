package org.eclipse.jakarta.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class SimulationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException e) {
        int status = e.getResponse().getStatus();
        String message = status == 404
                ? "A required resource was not found: " + e.getMessage()
                : "Upstream service error during simulation: " + e.getMessage();
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", message))
                .build();
    }
}
