package net.bdavies.metrics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import net.bdavies.metrics.model.ApiUser;
import net.bdavies.metrics.repository.ApiUserRepository;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ApiUserRepository repository;
    private ApiUser user;

    @BeforeEach
    void setup() {
        user = repository.saveAndFlush(ApiUser.builder()
                .username("testUser")
                .build());
    }

    @Test
    @DisplayName("WhoAmi Returns User")
    void testWhoAmi() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/auth/whoami")
                        .with(csrf())
                        .queryParam("apiKey", user.getApiKey().toString())
                )
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        String userAsJson = mapper.writeValueAsString(user);
        assertEquals(userAsJson, result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("WhoAmi Fails When Api Key not provided")
    void testWhoAmiFailsWhenNoApi() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/auth/whoami")
                        .with(csrf())
                )
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("API Key missing"));
    }

    @Test
    @DisplayName("WhoAmi Fails When Api Key is wrong")
    void testWhoAmiFailsWhenKeyIsWrong() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/auth/whoami")
                        .queryParam("apiKey", UUID.randomUUID().toString())
                        .with(csrf())
                )
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("API Key not valid"));
    }
}
