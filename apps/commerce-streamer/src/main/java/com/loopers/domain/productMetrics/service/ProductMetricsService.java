package com.loopers.domain.productMetrics.service;

import static com.loopers.support.error.ErrorType.INTERNAL_ERROR;

import com.loopers.domain.productMetrics.dto.ProductMetricsCommand;
import com.loopers.domain.productMetrics.entity.ProductMetricsEntity;
import com.loopers.domain.productMetrics.repository.ProductMetricsRepository;

import com.loopers.support.error.CoreException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
     * 일별 메트릭스 upsert 해당 날짜에 기록이 있으면 업데이트, 없으면 생성
     */
    @Transactional
    public void upsertDailyMetrics(ProductMetricsCommand.Upsert command) {
        try {
            tryUpsertDailyMetrics(command);
        } catch (DataIntegrityViolationException e) {
            // 고유 제약 조건 위반 발생 시 다시 시도
            // 이미 다른 스레드가 생성했을 것이므로, update 진행
            Optional<ProductMetricsEntity> existingMetrics =
                productMetricsRepository.findByProductIdAndMetricsDate(command.productId(), command.metricsDate());

            if (existingMetrics.isPresent()) {
                ProductMetricsEntity metrics = existingMetrics.get();
                metrics.incrementMetrics(command.likeCountDelta(), command.viewCountDelta(),
                    command.salesCountDelta());
                productMetricsRepository.save(metrics);
            } else {
                // 여전히 찾을 수 없다면 예외 상황 - 로그 기록 후 다시 예외 발생
                throw new CoreException(INTERNAL_ERROR, "메트릭 데이터 업서트 실패: " + e.getMessage());
            }
        }
    }

    private void tryUpsertDailyMetrics(ProductMetricsCommand.Upsert command) {
        Optional<ProductMetricsEntity> existingMetrics =
            productMetricsRepository.findByProductIdAndMetricsDate(command.productId(), command.metricsDate());

        if (existingMetrics.isPresent()) {
            // 기존 기록 업데이트
            ProductMetricsEntity metrics = existingMetrics.get();
            metrics.incrementMetrics(command.likeCountDelta(), command.viewCountDelta(),
                command.salesCountDelta());
            productMetricsRepository.save(metrics);
        } else {
            // 새로운 기록 생성
            ProductMetricsEntity newMetrics = ProductMetricsEntity.createForDate(command.productId(), command.metricsDate());
            newMetrics.incrementMetrics(command.likeCountDelta(), command.viewCountDelta(),
                command.salesCountDelta());
            productMetricsRepository.save(newMetrics);
        }
    }

    @Transactional
    public void increaseDailyLikeCount(ProductMetricsCommand.IncreaseLikeCount command) {
        // 좋아요 수만 1 증가시키고 나머지 메트릭은 변경하지 않음
        upsertDailyMetrics(
            new ProductMetricsCommand.Upsert(
                command.productId(),
                LocalDate.now(),
                1L,  // 좋아요 1 증가
                0L,  // 조회 수 변화 없음
                0L   // 판매 수 변화 없음
            )
        );
    }

    @Transactional
    public void decreaseDailyLikeCount(ProductMetricsCommand.DecreaseLikeCount command) {
        // 좋아요 수만 1 감소시키고 나머지 메트릭은 변경하지 않음
        upsertDailyMetrics(
            new ProductMetricsCommand.Upsert(
                command.productId(),
                LocalDate.now(),
                -1L,  // 좋아요 1 감소
                0L,  // 조회 수 변화 없음
                0L   // 판매 수 변화 없음
            )
        );
    }

    @Transactional
    public void increaseProductViewedCount(ProductMetricsCommand.IncreaseProductViewedCount command) {
        // 조회 수만 1 증가시키고 나머지 메트릭은 변경하지 않음
        upsertDailyMetrics(
            new ProductMetricsCommand.Upsert(
                command.productId(),
                LocalDate.now(),
                0L,  // 좋아요 수 변화 없음
                1L,  // 조회 수 1 증가
                0L   // 판매 수 변화 없음
            )
        );
    }

    @Transactional
    public void increaseOrderPaymentCount(ProductMetricsCommand.IncreaseOrderPaymentCount command) {
        // 판매 수만 1 증가시키고 나머지 메트릭은 변경하지 않음
        upsertDailyMetrics(
            new ProductMetricsCommand.Upsert(
                command.productId(),
                LocalDate.now(),
                0L,  // 좋아요 수 변화 없음
                0L,  // 조회 수 변화 없음
                1L   // 판매 수 1 증가
            )
        );
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
