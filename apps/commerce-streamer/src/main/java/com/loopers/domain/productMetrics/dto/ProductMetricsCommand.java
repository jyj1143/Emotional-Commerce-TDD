package com.loopers.domain.productMetrics.dto;

import com.loopers.domain.productMetrics.entity.ProductMetricsEntity;
import java.time.LocalDate;

public record ProductMetricsCommand() {

    public record Create(
        Long productId
    ) {
        public ProductMetricsEntity toEntity() {
            return ProductMetricsEntity.create(productId);
        }
    }

    public record Upsert(
        Long productId,
        LocalDate metricsDate,
        Long likeCountDelta,
        Long viewCountDelta,
        Long salesCountDelta
    ) {
    }

    public record IncreaseLikeCount(
        Long productId
    ) {}

    public record DecreaseLikeCount(
        Long productId
    ) {}

    public record IncreaseProductClickCount(
        Long productId
    ) {}

    public record IncreaseProductViewedCount(
        Long productId
    ) {}

    public record IncreaseOrderPaymentCount(
        Long productId
    ) {}

}
