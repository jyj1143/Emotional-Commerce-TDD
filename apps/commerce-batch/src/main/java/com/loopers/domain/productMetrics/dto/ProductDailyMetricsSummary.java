package com.loopers.domain.productMetrics.dto;

import java.time.LocalDate;

public record ProductDailyMetricsSummary(
    Long productId,    // String 타입
    LocalDate metricsDate,
    Long likeCount,
    Long viewCount,
    Long salesCount
) {}

