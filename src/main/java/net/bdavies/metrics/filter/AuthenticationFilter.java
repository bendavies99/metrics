package net.bdavies.metrics.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.bdavies.metrics.dto.RequestError;
import net.bdavies.metrics.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * A Spring Web Filter to intercept a request to ensure the client is authenticated using an API Key
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends GenericFilterBean {
    private final AuthenticationService service;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            Authentication auth = service.authenticate((HttpServletRequest) request);
            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(request, response);
        } catch (Exception e) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = httpResponse.getWriter();
            HttpServletRequest req = (HttpServletRequest) request;
            RequestError error = RequestError.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .path(req.getServletPath())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error(e.getClass().getSimpleName().replace("Exception", ""))
                    .message(e.getMessage())
                    .build();
            writer.print(new ObjectMapper()
                    .writeValueAsString(error));
            writer.flush();
            writer.close();
        }
    }
}
