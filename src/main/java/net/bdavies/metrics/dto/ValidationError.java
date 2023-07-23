package net.bdavies.metrics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO when there is a validation error from wrong data inputted from the Client used here
 * {@link net.bdavies.metrics.configuration.ConstraintViolationExceptionHandler}
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationError {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final Map<String, String> violations;
    private final String path;
}
