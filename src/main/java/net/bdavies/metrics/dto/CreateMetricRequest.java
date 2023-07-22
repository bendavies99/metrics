package net.bdavies.metrics.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateMetricRequest {
    @NotEmpty
    private final String system;
    @NotEmpty
    private final String name;
    @Builder.Default
    private final int date = (int) Instant.now().getEpochSecond();
    @Builder.Default
    private final int value = 1;
}
