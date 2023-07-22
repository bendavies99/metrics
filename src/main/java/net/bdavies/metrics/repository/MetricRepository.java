package net.bdavies.metrics.repository;

import net.bdavies.metrics.model.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Metric}
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Repository
public interface MetricRepository extends JpaRepository<Metric, Integer> {
    List<Metric> findBySystem(String system);

    Optional<Metric> findBySystemAndNameAndDate(String system, String name, int date);
}
