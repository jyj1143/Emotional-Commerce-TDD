package com.loopers.domain.productMetrics.service;

import com.loopers.domain.productMetrics.dto.ProductMetricsCommand;
import com.loopers.domain.productMetrics.entity.ProductMetricsEntity;
import com.loopers.domain.productMetrics.repository.ProductMetricsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductMetricsService {

    private final ProductMetricsRepository productMetricsRepository;

    @Transactional
    public void save(ProductMetricsCommand.Create command) {
        Optional<ProductMetricsEntity> existingMetrics =
            productMetricsRepository.findByProductId(command.productId());
        if (existingMetrics.isEmpty()) {
            productMetricsRepository.save(command.toEntity());
        }
    }
}
