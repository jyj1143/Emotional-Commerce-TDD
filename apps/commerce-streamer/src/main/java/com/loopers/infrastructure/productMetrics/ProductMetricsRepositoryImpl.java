package com.loopers.infrastructure.productMetrics;

import com.loopers.domain.productMetrics.entity.ProductMetricsEntity;
import com.loopers.domain.productMetrics.repository.ProductMetricsRepository;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductMetricsRepositoryImpl implements ProductMetricsRepository {
    private final ProductMetricsJpaRepository productMetricsJpaRepository;

    @Override
    public ProductMetricsEntity save(ProductMetricsEntity productMetrics) {
        return productMetricsJpaRepository.save(productMetrics);
    }
    @Override
    public Optional<ProductMetricsEntity> findByProductId(Long id) {
        return productMetricsJpaRepository.findByProductId(id);
    }

    @Override
    public Optional<ProductMetricsEntity> findByDaily(Long productId) {
        return Optional.empty();
    }


}
