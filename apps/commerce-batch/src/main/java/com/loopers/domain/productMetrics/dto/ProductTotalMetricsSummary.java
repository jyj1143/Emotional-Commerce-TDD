package com.loopers.domain.productMetrics.dto;

public record ProductTotalMetricsSummary(
    Long productId,
    Long likeCount,
    Long viewCount,
    Long salesCount
) {}
