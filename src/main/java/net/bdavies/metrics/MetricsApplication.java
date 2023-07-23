package net.bdavies.metrics;

import lombok.extern.slf4j.Slf4j;
import net.bdavies.metrics.model.ApiUser;
import net.bdavies.metrics.repository.ApiUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


/**
 * Metrics Application Entry Point
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@SpringBootApplication
@Slf4j
public class MetricsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetricsApplication.class, args);
    }

    @Bean
    CommandLineRunner onBoot(ApiUserRepository repository) {
        return args -> {
            if (repository.findAll().isEmpty()) {
                ApiUser user = repository.save(ApiUser.builder()
                        .username("testAccount")
                        .build());
                log.info("""
                                                
                        ***********************************
                        Admin Account API Key: {}
                        SAVE THIS FOR FUTURE REFERENCE
                        ***********************************
                        """, user.getApiKey());
            }
        };
    }
}
