package net.bdavies.metrics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Request DTO for retrieving a list of {@link net.bdavies.metrics.model.Metric}s from the system
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetMetricsRequest {
    private final String system;
    private final String name;
    private final Integer from;
    private final Integer to;
}
