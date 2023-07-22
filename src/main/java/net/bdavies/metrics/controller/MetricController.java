package net.bdavies.metrics.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import net.bdavies.metrics.dto.GetMetricsRequest;
import net.bdavies.metrics.model.Metric;
import net.bdavies.metrics.service.MetricService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/metrics")
public class MetricController {
    private final MetricService service;

    @GetMapping("/")
    public List<Metric> metrics(
            @RequestParam("system") @NotEmpty String system,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "to", required = false) Integer to
    ) {
        return service.getAll(GetMetricsRequest.builder()
                .system(system)
                .name(name)
                .from(from)
                .to(to)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Metric> metricById(
            @PathVariable("id") @Min(1) @Max(Integer.MAX_VALUE) int id
    ) {
        Optional<Metric> metric = service.getById(id);
        return metric
                .map(value -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(value))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(null));
    }
}
