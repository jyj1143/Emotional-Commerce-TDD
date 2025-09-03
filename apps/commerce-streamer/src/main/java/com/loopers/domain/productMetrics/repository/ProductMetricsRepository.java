package com.loopers.domain.productMetrics.repository;

import com.loopers.domain.productMetrics.entity.ProductMetricsEntity;
import java.time.ZonedDateTime;
import java.util.Optional;

public interface ProductMetricsRepository {

    ProductMetricsEntity save(ProductMetricsEntity productMetrics);

    Optional<ProductMetricsEntity> findByProductId(Long productId);

    Optional<ProductMetricsEntity> findByDaily(Long productId);
}
