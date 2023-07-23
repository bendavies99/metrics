package net.bdavies.metrics.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import net.bdavies.metrics.dto.GetMetricsRequest;
import net.bdavies.metrics.dto.MetricSummary;
import net.bdavies.metrics.service.MetricService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller for getting a {@link MetricSummary} of a particular Metric
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@Validated
public class MetricSummaryController {
    private final MetricService service;

    @GetMapping("/metricsummary")
    public MetricSummary metricSummary(
            @RequestParam("system") @NotEmpty String system,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "to", required = false) Integer to
    ) {
        return service.getMetricSummary(GetMetricsRequest.builder()
                .system(system)
                .name(name)
                .from(from)
                .to(to)
                .build());
    }
}
