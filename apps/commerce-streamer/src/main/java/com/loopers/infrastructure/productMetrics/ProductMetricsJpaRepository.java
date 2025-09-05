package com.loopers.infrastructure.productMetrics;

import com.loopers.domain.productMetrics.entity.ProductMetricsEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetricsEntity, Long> {
    Optional<ProductMetricsEntity> findByProductId(Long productId);

    Optional<ProductMetricsEntity> findByProductIdAndMetricsDate(Long productId, LocalDate metricsDate);

    List<ProductMetricsEntity> findByMetricsDateBetween(LocalDate startDate, LocalDate endDate);

    List<ProductMetricsEntity> findByProductIdAndMetricsDateBetween(Long productId, LocalDate startDate, LocalDate endDate);

}
