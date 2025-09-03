package com.loopers.infrastructure.productMetrics;

import com.loopers.domain.productMetrics.entity.ProductMetricsEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetricsEntity, Long> {
    Optional<ProductMetricsEntity> findByProductId(Long productId);
}
