package com.magmutual.users.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magmutual.users.model.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;

public class ResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Writes an error response to the HttpServletResponse.
     *
     * @param response the HttpServletResponse object to write the response to
     * @param status the HTTP status code
     * @param message the error message
     * @param details additional details about the error
     * @throws IOException if an input or output exception occurred
     */
    public static void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message, String details) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        ErrorResponse errorResponse = new ErrorResponse(message, details);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
