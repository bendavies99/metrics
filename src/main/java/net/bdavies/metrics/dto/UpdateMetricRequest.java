package net.bdavies.metrics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Request DTO for updating an existing {@link net.bdavies.metrics.model.Metric} with added validation to
 * ensure the client enters the correct information
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateMetricRequest {
    @NotEmpty
    private final String system;
    @NotEmpty
    private final String name;
    @NotNull
    private final Integer date;
    private final Integer value;
}
