package net.bdavies.metrics.configuration;

import lombok.RequiredArgsConstructor;
import net.bdavies.metrics.filter.AuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration for Spring Security this will disable several defaults such as:
 *  - Form Login
 *  - Http Basic Auth
 * <p>
 * Then Setup Stateless Authentication because our Application is Stateless it will use
 * the {@link AuthenticationFilter} to ensure that the client is authenticated on request
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfiguration {
    private final AuthenticationFilter filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(req ->
                req.anyRequest().authenticated());
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
