package net.bdavies.metrics.service;

import jakarta.servlet.http.HttpServletRequest;
import net.bdavies.metrics.model.ApiUser;
import net.bdavies.metrics.repository.ApiUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@SpringBootTest
public class AuthenticationServiceTest {
    @InjectMocks
    AuthenticationService service;

    @Mock
    ApiUserRepository repository;

    @Test
    @DisplayName("Block Request when no API Key Provided")
    void testExceptionOnNoApiKey() {
        assertThrows(BadCredentialsException.class, () -> {
            service.authenticate(mockRequest(null));
        });
    }

    @Test
    @DisplayName("Block Request when no API User Found with key")
    void testExceptionOnInvalidApiKey() {
        when(repository.findByApiKey(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(BadCredentialsException.class, () -> {
            service.authenticate(mockRequest(null));
        });
    }

    @Test
    @DisplayName("Block Request when API Key not valid UUID")
    void testExceptionOnInvalidUUid() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.authenticate(mockRequest("1212121"));
        });
    }

    @Test
    @DisplayName("Accept Request when API Key Matches")
    void testRequestAcceptedOnValidKey() {
        when(repository.findByApiKey(any(UUID.class))).thenReturn(Optional.of(
                ApiUser.builder().build()
        ));
        Authentication auth = service.authenticate(mockRequest(UUID.randomUUID().toString()));
        assertNotNull(auth);
    }

    private HttpServletRequest mockRequest(String apiKey) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("apiKey")).thenReturn(apiKey);
        return request;
    }
}
