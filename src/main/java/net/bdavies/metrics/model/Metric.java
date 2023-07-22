package net.bdavies.metrics.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

/**
 * Model for a Metric
 * <p>
 * Each metric is defined by a name and records a count at a specific time.
 * The exact purpose of these metrics is not defined here.
 * <p>
 * This service may be used by other applications to record and report on their metrics.
 *
 * @author me@bdavies (Ben Davies)
 * @since 1.0.0
 */
@Entity
@Data
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"system", "name", "date"}))
public class Metric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final int id;
    private final String system;
    private final String name;
    private final int date;
    private final int value;
}
