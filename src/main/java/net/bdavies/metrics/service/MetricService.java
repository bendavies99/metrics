package net.bdavies.metrics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.metrics.dto.CreateMetricRequest;
import net.bdavies.metrics.dto.GetMetricsRequest;
import net.bdavies.metrics.dto.MetricSummary;
import net.bdavies.metrics.dto.UpdateMetricRequest;
import net.bdavies.metrics.model.Metric;
import net.bdavies.metrics.repository.MetricRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The service handling the business logic for the {@link net.bdavies.metrics.controller.MetricController}
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricService {
    private final MetricRepository repository;

    public List<Metric> getAll(GetMetricsRequest req) {
        return filterByMetricsRequest(repository.findBySystem(req.getSystem()), req);
    }

    public MetricSummary getMetricSummary(GetMetricsRequest req) {
        List<Metric> metrics = getAll(req);
        String metricNames = metrics.stream().map(Metric::getName)
                .distinct().collect(Collectors.joining(", "));
        int totalValue = metrics.stream().mapToInt(Metric::getValue).sum();
        int from = metrics.stream().mapToInt(Metric::getDate).min().orElse(0);
        int to = metrics.stream().mapToInt(Metric::getDate).max().orElse(0);
        return MetricSummary.builder()
                .system(req.getSystem())
                .name(metricNames)
                .from(from)
                .to(to)
                .value(totalValue)
                .build();
    }

    public Optional<Metric> getById(int id) {
        return repository.findById(id);
    }

    public Metric createMetric(CreateMetricRequest req) {
        Optional<Metric> metric = repository.findBySystemAndNameAndDate(req.getSystem(), req.getName(), req.getDate());

        if (metric.isPresent()) {
            log.info("Trying to create a metric that already exists will return the already existing metric req: {}", req);
            return metric.get();
        }

        return repository.saveAndFlush(Metric.builder()
                .system(req.getSystem())
                .name(req.getName())
                .date(req.getDate())
                .value(req.getValue())
                .build());
    }

    public Optional<Metric> updateMetric(int id, UpdateMetricRequest req) {
        Optional<Metric> metricOpt = getById(id);
        if (metricOpt.isEmpty()) {
            return metricOpt;
        }

        Metric metric = metricOpt.get();
        metric.setSystem(req.getSystem());
        metric.setName(req.getName());
        metric.setDate(req.getDate());
        metric.setValue(getNewMetricValue(metric.getValue(), req));

        return Optional.of(repository.save(metric));
    }

    private int getNewMetricValue(int current, UpdateMetricRequest req) {
        if (req.getValue() != null && req.getValue() != 0) {
            return req.getValue();
        }

        return current + 1;
    }

    private List<Metric> filterByMetricsRequest(List<Metric> metrics, GetMetricsRequest req) {
        var metricsStream = metrics.stream();
        if (req.getName() != null && !req.getName().isEmpty()) {
            metricsStream = metricsStream.filter(metric -> metric.getName().equals(req.getName()));
        }

        if (req.getFrom() != null && req.getFrom() != 0) {
            metricsStream = metricsStream.filter(metric -> metric.getDate() >= req.getFrom());
        }

        if (req.getTo() != null && req.getTo() != 0) {
            metricsStream = metricsStream.filter(metric -> metric.getDate() < req.getTo());
        }

        return metricsStream.collect(Collectors.toList());
    }
}
