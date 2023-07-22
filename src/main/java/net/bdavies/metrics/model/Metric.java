package net.bdavies.metrics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metric", uniqueConstraints = @UniqueConstraint(columnNames = {"system", "name", "date"}))
public class Metric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String system;
    private String name;
    private int date;
    @Column(name = "metric_value")
    private int value;
}
