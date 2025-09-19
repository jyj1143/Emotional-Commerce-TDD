package com.loopers.domain.productMetrics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(
    name = "product_metrics",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_product_metrics_date",
            columnNames = {"ref_product_id", "metrics_date"}
        )
    }
)
public class ProductMetricsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_product_id", nullable = false)
    private Long productId;

    @Column(name = "metrics_date", nullable = false)
    private LocalDate metricsDate;

    /**
     * 집계 관련 필드 (일별)
     */
    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "sales_count", nullable = false)
    private Long salesCount = 0L;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}
