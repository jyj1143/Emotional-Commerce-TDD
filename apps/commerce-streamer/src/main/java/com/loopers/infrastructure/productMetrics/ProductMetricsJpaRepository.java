package com.loopers.infrastructure.productMetrics;

import com.loopers.domain.productMetrics.entity.ProductMetricsEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetricsEntity, Long> {
    Optional<ProductMetricsEntity> findByProductId(Long productId);

    Optional<ProductMetricsEntity> findByProductIdAndMetricsDate(Long productId, LocalDate metricsDate);

    List<ProductMetricsEntity> findByMetricsDateBetween(LocalDate startDate, LocalDate endDate);

    List<ProductMetricsEntity> findByProductIdAndMetricsDateBetween(Long productId, LocalDate startDate, LocalDate endDate);

}
