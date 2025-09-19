package com.loopers.domain.productMetrics.repository;

import com.loopers.domain.productMetrics.dto.ProductDailyMetricsSummary;
import com.loopers.domain.productMetrics.dto.ProductTotalMetricsSummary;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductMetricsRepository {

    List<ProductDailyMetricsSummary> findDailySummary(LocalDate startDate, LocalDate endDate);

    Page<ProductTotalMetricsSummary> findTotalSummary(LocalDate startDate, LocalDate endDate, Pageable pageRequest);
}
