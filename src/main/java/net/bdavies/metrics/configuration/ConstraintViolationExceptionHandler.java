package net.bdavies.metrics.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import net.bdavies.metrics.dto.ValidationError;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception Handler for Handling Validation Exceptions from the Controller Method Parameters for the Request Body
 * there is another handler which is handled by Spring
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */

@ControllerAdvice
@Configuration
public class ConstraintViolationExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationError> handleException(HttpServletRequest request, ConstraintViolationException ex) {
        Map<String, String> violations = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(cv -> violations.put(cv.getPropertyPath().toString(), cv.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ValidationError.builder()
                        .timestamp(LocalDateTime.now())
                        .error("Bad Request")
                        .violations(violations)
                        .path(request.getServletPath())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build());
    }
}
