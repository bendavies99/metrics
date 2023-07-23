package net.bdavies.metrics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import net.bdavies.metrics.dto.MetricSummary;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class MetricSummaryControllerTest {
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
        metricRepository.save(createMetric("Test", "uptime"));
        metricRepository.save(createMetric("Test", "lastExceptionInMs"));
    }

    @Test
    @DisplayName("GET /metricsummary?system=Test (200)")
    void metricSummary1() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/metricsummary")
                        .with(csrf())
                        .queryParam("apiKey", user.getApiKey().toString())
                        .queryParam("system", "Test")
                )
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        MetricSummary summary = mapper.readValue(result.getResponse().getContentAsString(), MetricSummary.class);
        assertEquals(2, summary.getValue());
    }

    @Test
    @DisplayName("GET /metricsummary (400 because system missing)")
    void metricSummary2() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/metricsummary")
                        .with(csrf())
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

    }


    @Test
    @DisplayName("GET /metricsummary?system (400 because system is empty)")
    void metricSummary3() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/metricsummary")
                        .with(csrf())
                        .queryParam("apiKey", user.getApiKey().toString())
                        .queryParam("system", "")
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
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
