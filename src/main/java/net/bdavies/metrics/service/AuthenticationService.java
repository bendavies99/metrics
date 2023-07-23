package net.bdavies.metrics.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.bdavies.metrics.model.ApiUser;
import net.bdavies.metrics.repository.ApiUserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * A service to check whether a {@link HttpServletRequest} contains the correct information to
 * authenticate a client
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final String API_KEY_QUERY_STRING = "apiKey";

    private final ApiUserRepository repository;

    public Authentication authenticate(HttpServletRequest request) {
        String apiKey = request.getParameter(API_KEY_QUERY_STRING);
        if (apiKey == null || "".equals(apiKey)) {
            throw new BadCredentialsException("API Key missing");
        }

        Optional<ApiUser> user = repository.findByApiKey(UUID.fromString(apiKey));
        if (user.isEmpty()) {
            throw new BadCredentialsException("API Key not valid");
        }
        UserDetails details = user.get();
        return new UsernamePasswordAuthenticationToken(details,
                null, details.getAuthorities());
    }
}
