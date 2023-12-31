package net.bdavies.metrics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Response DTO for the {@link net.bdavies.metrics.controller.MetricSummaryController}
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricSummary {
    private final String system;
    private final String name;
    private final int from;
    private final int to;
    private final int value;
}
