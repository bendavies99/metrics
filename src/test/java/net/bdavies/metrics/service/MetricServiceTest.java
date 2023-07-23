package net.bdavies.metrics.service;

import net.bdavies.metrics.dto.CreateMetricRequest;
import net.bdavies.metrics.dto.GetMetricsRequest;
import net.bdavies.metrics.dto.MetricSummary;
import net.bdavies.metrics.dto.UpdateMetricRequest;
import net.bdavies.metrics.model.Metric;
import net.bdavies.metrics.repository.MetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@SpringBootTest
public class MetricServiceTest {
    public static final String USER_SERVICE = "UserService";
    public static final String STREAMING_SERVICE = "StreamingService";
    @InjectMocks
    MetricService metricService;

    @Mock
    MetricRepository repository;

    List<Metric> userServiceMetrics;
    List<Metric> streamingServiceMetrics;
    int epochNow;

    @BeforeEach
    void setup() {
        epochNow = (int) Instant.now().getEpochSecond();
        userServiceMetrics = List.of(
                createMetric(USER_SERVICE, "loginAttempts"),
                createMetric(USER_SERVICE, "failedLoginAttempts"),
                createMetric(USER_SERVICE, "uptime")
        );
        streamingServiceMetrics = List.of(
                createMetric(STREAMING_SERVICE, "uptime")
        );

        when(repository.saveAndFlush(any(Metric.class))).thenAnswer(a -> a.getArgument(0));
        when(repository.save(any(Metric.class))).thenAnswer(a -> a.getArgument(0));
        when(repository.findBySystem(USER_SERVICE)).thenReturn(userServiceMetrics);
        when(repository.findBySystem(STREAMING_SERVICE)).thenReturn(streamingServiceMetrics);
    }

    @Test
    @DisplayName("Test Get all Metrics No filter")
    void testGetAll() {
        List<Metric> resp = metricService.getAll(GetMetricsRequest.builder()
                .system(USER_SERVICE)
                .build());
        assertEquals(userServiceMetrics.size(), resp.size());
        assertEquals(userServiceMetrics.get(0).getName(), resp.get(0).getName());
    }

    @Test
    @DisplayName("Test Get all Metrics Using a different system")
    void testGetAllUsingADifferentSystem() {
        List<Metric> resp = metricService.getAll(GetMetricsRequest.builder()
                .system(STREAMING_SERVICE)
                .build());
        assertEquals(streamingServiceMetrics.size(), resp.size());
        assertEquals(streamingServiceMetrics.get(0).getName(), resp.get(0).getName());
    }

    @Test
    @DisplayName("Test Get all Metrics Filter By Name")
    void testGetAllByName() {
        List<Metric> resp = metricService.getAll(GetMetricsRequest.builder()
                .system(USER_SERVICE)
                .name("uptime")
                .build());
        assertEquals(1, resp.size());
        assertEquals("uptime", resp.get(0).getName());
    }

    @Test
    @DisplayName("Test Get all Metrics Filter From a date")
    void testGetAllFrom() {
        List<Metric> resp = metricService.getAll(GetMetricsRequest.builder()
                .system(USER_SERVICE)
                .from(epochNow - 10)
                .build());
        assertEquals(userServiceMetrics.size(), resp.size());

        resp = metricService.getAll(GetMetricsRequest.builder()
                .system(USER_SERVICE)
                .from(epochNow)
                .build());
        assertEquals(userServiceMetrics.size(), resp.size());

        resp = metricService.getAll(GetMetricsRequest.builder()
                .system(USER_SERVICE)
                .from(epochNow + 1)
                .build());
        assertEquals(0, resp.size());
    }

    @Test
    @DisplayName("Test Get all Metrics Filter To a date")
    void testGetAllTo() {
        List<Metric> resp = metricService.getAll(GetMetricsRequest.builder()
                .system(USER_SERVICE)
                .to(epochNow + 10)
                .build());
        assertEquals(userServiceMetrics.size(), resp.size());

        resp = metricService.getAll(GetMetricsRequest.builder()
                .system(USER_SERVICE)
                .to(epochNow)
                .build());
        assertEquals(0, resp.size());

        resp = metricService.getAll(GetMetricsRequest.builder()
                .system(USER_SERVICE)
                .to(epochNow - 1)
                .build());
        assertEquals(0, resp.size());
    }

    @Test
    @DisplayName("Test Get Metric Summary")
    void testGetMetricSummary() {
        MetricSummary summary = metricService.getMetricSummary(GetMetricsRequest.builder()
                .system(USER_SERVICE)
                .build());
        assertEquals("loginAttempts, failedLoginAttempts, uptime", summary.getName());
        assertEquals(3, summary.getValue());
        assertEquals(epochNow, summary.getFrom());
        assertEquals(epochNow, summary.getTo());
    }

    @Test
    @DisplayName("Test Create a Unique Metric")
    void testCreateMetric() {
        when(repository.findBySystemAndNameAndDate(anyString(), anyString(), anyInt()))
                .thenReturn(Optional.empty());

        Metric metric = metricService
                .createMetric(CreateMetricRequest.builder()
                        .system(USER_SERVICE)
                        .date((int) Instant.now().getEpochSecond())
                        .name("testMetric")
                        .build());
        assertEquals(1, metric.getValue());
        verify(repository).saveAndFlush(metric);
    }

    @Test
    @DisplayName("Test Create a non-unique Metric (so it will return the old one)")
    void testCreateMetricNonUnique() {
        Metric metricFound = createMetric(USER_SERVICE, "testMetric");
        when(repository.findBySystemAndNameAndDate(anyString(), anyString(), anyInt()))
                .thenReturn(Optional.of(metricFound));

        Metric metric = metricService
                .createMetric(CreateMetricRequest.builder()
                        .system(USER_SERVICE)
                        .date((int) Instant.now().getEpochSecond())
                        .name("testMetric")
                        .build());
        assertEquals(metricFound, metric);
        verify(repository, times(0)).saveAndFlush(any(Metric.class));
    }

    @Test
    @DisplayName("Test Get Metric by Id")
    void testGetMetricById() {
        final int ID = 1234;
        when(repository.findById(ID)).thenReturn(Optional.of(createMetric(USER_SERVICE, "testMetric")));
        Optional<Metric> metric = metricService.getById(ID);
        verify(repository).findById(ID);
        assertTrue(metric.isPresent());
        assertEquals("testMetric", metric.get().getName());
    }

    @Test
    @DisplayName("Update a metric and auto increments the value by 1 if not supplied")
    void updateMetricAutoIncrement() {
        final int ID = 1234;
        when(repository.findById(ID)).thenReturn(Optional.of(createMetric(USER_SERVICE, "testMetric")));
        Optional<Metric> metric = metricService.updateMetric(ID, UpdateMetricRequest.builder()
                .system(USER_SERVICE)
                .name("testMetric2")
                .date(epochNow)
                .build());

        assertTrue(metric.isPresent());
        verify(repository).save(metric.get());
        assertEquals("testMetric2", metric.get().getName());
        assertEquals(2, metric.get().getValue());
    }

    @Test
    @DisplayName("Update a metric and doesn't increment value when supplied")
    void updateMetricNonAutoIncrement() {
        final int ID = 1234;
        final int VALUE = 50;
        when(repository.findById(ID)).thenReturn(Optional.of(createMetric(USER_SERVICE, "testMetric")));
        Optional<Metric> metric = metricService.updateMetric(ID, UpdateMetricRequest.builder()
                .system(USER_SERVICE)
                .name("testMetric2")
                .date(epochNow)
                .value(VALUE)
                .build());

        assertTrue(metric.isPresent());
        verify(repository).save(metric.get());
        assertEquals("testMetric2", metric.get().getName());
        assertEquals(VALUE, metric.get().getValue());
    }


    @Test
    @DisplayName("Update a metric and doesn't exist so returns empty Optional")
    void updateMetricEmptyOptional() {
        final int ID = 1234;
        when(repository.findById(ID)).thenReturn(Optional.empty());
        Optional<Metric> metric = metricService.updateMetric(ID, UpdateMetricRequest.builder()
                .system(USER_SERVICE)
                .name("testMetric2")
                .date(epochNow)
                .build());

        verify(repository, times(0)).save(any(Metric.class));
        assertFalse(metric.isPresent());
    }

    Metric createMetric(String system, String name) {
        return Metric.builder()
                .system(system)
                .name(name)
                .date(epochNow)
                .value(1)
                .build();
    }
}
