package net.bdavies.metrics.service;

import lombok.RequiredArgsConstructor;
import net.bdavies.metrics.dto.CreateMetricRequest;
import net.bdavies.metrics.dto.GetMetricsRequest;
import net.bdavies.metrics.dto.UpdateMetricRequest;
import net.bdavies.metrics.model.Metric;
import net.bdavies.metrics.repository.MetricRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class MetricService {
    private final MetricRepository repository;

    public List<Metric> getAll(GetMetricsRequest req) {
        return filterByMetricsRequest(repository.findBySystem(req.getSystem()), req);
    }

    public Optional<Metric> getById(int id) {
        return repository.findById(id);
    }

    public Metric createMetric(CreateMetricRequest req) {
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
