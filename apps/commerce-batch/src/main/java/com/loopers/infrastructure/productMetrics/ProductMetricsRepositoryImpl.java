package com.loopers.infrastructure.productMetrics;

import com.loopers.domain.productMetrics.dto.ProductDailyMetricsSummary;
import com.loopers.domain.productMetrics.dto.ProductTotalMetricsSummary;
import com.loopers.domain.productMetrics.repository.ProductMetricsRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductMetricsRepositoryImpl implements ProductMetricsRepository {

    private final ProductMetricsJpaRepository productMetricsJpaRepository;

    @Override
    public List<ProductDailyMetricsSummary> findDailySummary(LocalDate startDate, LocalDate endDate) {
        return productMetricsJpaRepository.findDailySummary(startDate, endDate);
    }

    @Override
    public Page<ProductTotalMetricsSummary> findTotalSummary(LocalDate startDate, LocalDate endDate, Pageable pageRequest) {
        return productMetricsJpaRepository.findTotalSummary(startDate, endDate, pageRequest);
    }

}
