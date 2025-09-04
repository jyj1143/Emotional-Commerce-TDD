package com.loopers.domain.productMetrics.service;

import com.loopers.domain.productMetrics.dto.ProductMetricsCommand;
import com.loopers.domain.productMetrics.entity.ProductMetricsEntity;
import com.loopers.domain.productMetrics.repository.ProductMetricsRepository;

import java.time.LocalDate;
import java.util.List;
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

    /**
     * 일별 메트릭스 upsert
     * 해당 날짜에 기록이 있으면 업데이트, 없으면 생성
     */
    @Transactional
    public void upsertDailyMetrics(Long productId, LocalDate metricsDate,
                                   Long likeCountDelta, Long clickCountDelta,
                                   Long viewCountDelta, Long salesCountDelta) {

        Optional<ProductMetricsEntity> existingMetrics =
                productMetricsRepository.findByProductIdAndMetricsDate(productId, metricsDate);

        if (existingMetrics.isPresent()) {
            // 기존 기록 업데이트
            ProductMetricsEntity metrics = existingMetrics.get();
            metrics.incrementMetrics(likeCountDelta, clickCountDelta, viewCountDelta, salesCountDelta);
            productMetricsRepository.save(metrics);
        } else {
            // 새로운 기록 생성
            ProductMetricsEntity newMetrics = ProductMetricsEntity.createForDate(productId, metricsDate);
            newMetrics.incrementMetrics(likeCountDelta, clickCountDelta, viewCountDelta, salesCountDelta);
            productMetricsRepository.save(newMetrics);
        }
    }


    /**
     * 오늘 날짜로 메트릭스 upsert
     */
    @Transactional
    public void upsertTodayMetrics(Long productId, Long likeCountDelta, Long clickCountDelta,
                                   Long viewCountDelta, Long salesCountDelta) {
        upsertDailyMetrics(productId, LocalDate.now(), likeCountDelta, clickCountDelta,
                viewCountDelta, salesCountDelta);
    }

    /**
     * 특정 기간의 메트릭스 조회
     */
    @Transactional(readOnly = true)
    public List<ProductMetricsEntity> getMetricsByDateRange(Long productId, LocalDate startDate, LocalDate endDate) {
        return productMetricsRepository.findByProductIdAndMetricsDateBetween(productId, startDate, endDate);
    }

    /**
     * 특정 날짜의 메트릭스 조회
     */
    @Transactional(readOnly = true)
    public Optional<ProductMetricsEntity> getMetricsByDate(Long productId, LocalDate metricsDate) {
        return productMetricsRepository.findByProductIdAndMetricsDate(productId, metricsDate);
    }

}
