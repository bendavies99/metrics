package net.bdavies.metrics;

import net.bdavies.metrics.model.Metric;
import net.bdavies.metrics.repository.MetricRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;

@SpringBootApplication
public class MetricsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetricsApplication.class, args);
    }

    @Bean
    CommandLineRunner init(MetricRepository repository) {
        return args -> {
            repository.save(Metric.builder()
                    .system("Core")
                    .name("TimeToLive")
                    .date((int) Instant.now().getEpochSecond())
                    .value(1)
                    .build());
        };
    }

}
