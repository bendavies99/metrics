package net.bdavies.metrics.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import net.bdavies.metrics.dto.CreateMetricRequest;
import net.bdavies.metrics.dto.UpdateMetricRequest;
import net.bdavies.metrics.model.ApiUser;
import net.bdavies.metrics.model.Metric;
import net.bdavies.metrics.repository.ApiUserRepository;
import net.bdavies.metrics.repository.MetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Edit me
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class MetricControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ApiUserRepository repository;
    @Autowired
    private MetricRepository metricRepository;
    private ApiUser user;

    @BeforeEach
    void setup() {
        user = repository.saveAndFlush(ApiUser.builder()
                .username("testUser")
                .build());
        metricRepository.saveAndFlush(createMetric("Test", "uptime"));
        metricRepository.saveAndFlush(createMetric("Test", "lastExceptionInMs"));
    }

    @Test
    @DisplayName("GET /metrics?system=Test (200)")
    void metrics1() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/metrics")
                        .with(csrf())
                        .queryParam("apiKey", user.getApiKey().toString())
                        .queryParam("system", "Test")
                )
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        List<Metric> metrics = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(2, metrics.size());
    }

    @Test
    @DisplayName("GET /metrics (400 because system missing)")
    void metrics2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/metrics")
                        .with(csrf())
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

    }


    @Test
    @DisplayName("GET /metrics?system (400 because system is empty)")
    void metrics3() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/metrics")
                        .with(csrf())
                        .queryParam("apiKey", user.getApiKey().toString())
                        .queryParam("system", "")
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("GET /metrics/1 (200)")
    void metricsById1() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/metrics/1")
                        .with(csrf())
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /metrics/3323 (404 Not Found)")
    void metricsById2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/metrics/3323")
                        .with(csrf())
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /metrics/0 (400 because ids start at 1)")
    void metricsById3() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/metrics/0")
                        .with(csrf())
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /metrics (200)")
    void metricsCreate1() throws Exception {
        CreateMetricRequest request = CreateMetricRequest.builder()
                .system("ForTheTest")
                .name("uptime")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .post("/metrics")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().isOk())
                .andReturn();

        assertDoesNotThrow(() -> {
            mapper.readValue(result.getResponse().getContentAsString(), Metric.class);
        });
    }

    @Test
    @DisplayName("POST /metrics (400 because No Body Supplied)")
    void metricsCreate2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/metrics")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("POST /metrics (400 because Body validation failed)")
    void metricsCreate3() throws Exception {
        CreateMetricRequest request = CreateMetricRequest.builder()
                .system("ForTheTest")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(MockMvcRequestBuilders
                        .post("/metrics")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /metrics/1 (200)")
    void metricsUpdate1() throws Exception {
        UpdateMetricRequest request = UpdateMetricRequest.builder()
                .system("ForTheTest")
                .name("uptime")
                .date((int) Instant.now().getEpochSecond())
                .build();

        int firstId = metricRepository.findAll().get(0).getId();

        ObjectMapper mapper = new ObjectMapper();
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .put("/metrics/" + firstId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().isOk())
                .andReturn();

        assertDoesNotThrow(() -> {
            mapper.readValue(result.getResponse().getContentAsString(), Metric.class);
        });
    }


    @Test
    @DisplayName("PUT /metrics/2332 (404 Not Found)")
    void metricsUpdate2() throws Exception {
        UpdateMetricRequest request = UpdateMetricRequest.builder()
                .system("ForTheTest")
                .name("uptime")
                .date((int) Instant.now().getEpochSecond())
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(MockMvcRequestBuilders
                        .put("/metrics/2332")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("PUT /metrics/1 (400 because empty body)")
    void metricsUpdate3() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .put("/metrics/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("PUT /metrics/1 (400 because Invalid Body)")
    void metricsUpdate4() throws Exception {
        UpdateMetricRequest request = UpdateMetricRequest.builder()
                .system("ForTheTest")
                .name("uptime")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(MockMvcRequestBuilders
                        .put("/metrics/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().isBadRequest());
    }

    Metric createMetric(String system, String name) {
        return Metric.builder()
                .system(system)
                .name(name)
                .date((int) Instant.now().getEpochSecond())
                .value(1)
                .build();
    }
}
