package ee.taltech.iti03022024project.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;

@ControllerAdvice
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        // Set the response status to 403 Forbidden
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Optionally, you can write a custom message to the response body
        response.getWriter().write("Access denied: You do not have the necessary permissions to access this resource.");
    }
}
