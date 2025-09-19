package com.loopers.infrastructure.productMetrics;

import com.loopers.domain.productMetrics.dto.ProductDailyMetricsSummary;
import com.loopers.domain.productMetrics.dto.ProductTotalMetricsSummary;
import com.loopers.domain.productMetrics.entity.ProductMetricsEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetricsEntity, Long> {

    /**
     * 상품별 일자 단위 합계
     */
    @Query("""
        SELECT new com.loopers.domain.productMetrics.dto.ProductDailyMetricsSummary(
            pm.productId,
            pm.metricsDate,
            SUM(pm.likeCount),
            SUM(pm.viewCount),
            SUM(pm.salesCount)
        )
        FROM ProductMetricsEntity pm
        WHERE pm.metricsDate BETWEEN :startDate AND :endDate
        GROUP BY pm.productId, pm.metricsDate
    """)
    List<ProductDailyMetricsSummary> findDailySummary(LocalDate startDate, LocalDate endDate);

    /**
     * 상품별 전체 합계 (기간 전체 합산)
     */
    @Query("""
    SELECT new com.loopers.domain.productMetrics.dto.ProductTotalMetricsSummary(
        pm.productId,
        SUM(pm.likeCount),
        SUM(pm.viewCount),
        SUM(pm.salesCount)
    )
    FROM ProductMetricsEntity pm
    WHERE pm.metricsDate BETWEEN :startDate AND :endDate
    GROUP BY pm.productId
    """)
    Page<ProductTotalMetricsSummary> findTotalSummary(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
