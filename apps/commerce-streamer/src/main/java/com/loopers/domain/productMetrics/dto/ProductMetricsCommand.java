package com.loopers.domain.productMetrics.dto;

import com.loopers.domain.productMetrics.entity.ProductMetricsEntity;

public record ProductMetricsCommand() {

    public record Create(
        Long productId
    ) {
        public ProductMetricsEntity toEntity() {
            return ProductMetricsEntity.create(productId);
        }
    }

    public record IncreaseLikeCount(
        String productId
    ) {}

    public record DecreaseLikeCount(
        String productId
    ) {}
}
