package com.loopers.domain.productMetrics.repository;

import com.loopers.domain.productMetrics.entity.ProductMetricsEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductMetricsRepository {

    ProductMetricsEntity save(ProductMetricsEntity productMetrics);

    Optional<ProductMetricsEntity> findByProductId(Long productId);

    Optional<ProductMetricsEntity> findByProductIdAndMetricsDate(Long productId, LocalDate metricsDate);

    List<ProductMetricsEntity> findByProductIdAndMetricsDateBetween(Long productId, LocalDate startDate, LocalDate endDate);

}
