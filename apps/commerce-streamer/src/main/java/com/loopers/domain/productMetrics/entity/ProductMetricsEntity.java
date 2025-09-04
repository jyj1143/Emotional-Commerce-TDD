package com.loopers.domain.productMetrics.entity;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(
        name = "product_metrics",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_metrics_date",
                        columnNames = {"ref_product_id", "metrics_date"}
                )
        }
)
public class ProductMetricsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_product_id", nullable = false)
    private Long productId;

    @Column(name = "metrics_date", nullable = false)
    private LocalDate metricsDate;

    /**
     * 집계 관련 필드 (일별)
     */
    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "click_count", nullable = false)
    private Long clickCount = 0L;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "sales_count", nullable = false)
    private Long salesCount = 0L;

    /**
     * 타임스탬프 관련 필드
     */
    @Column(name = "last_liked_at")
    private LocalDateTime lastLikedAt;

    @Column(name = "last_un_liked_at")
    private LocalDateTime lastUnLikedAt;

    @Column(name = "last_clicked_at")
    private LocalDateTime lastClickedAt;

    @Column(name = "last_viewed_at")
    private LocalDateTime lastViewedAt;

    @Column(name = "last_sold_at")
    private LocalDateTime lastSoldAt;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    private ProductMetricsEntity(Long productId, LocalDate metricsDate) {
        this.productId = productId;
        this.metricsDate = metricsDate;
        this.likeCount = 0L;
        this.clickCount = 0L;
        this.viewCount = 0L;
        this.salesCount = 0L;
        this.lastSoldAt = null;
        this.lastClickedAt = null;
        this.lastViewedAt = null;
        this.lastLikedAt = null;
        this.lastUnLikedAt = null;
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }

    public static ProductMetricsEntity create(Long productId) {
        return new ProductMetricsEntity(productId, LocalDate.now());
    }

    public static ProductMetricsEntity createForDate(Long productId, LocalDate metricsDate) {
        return new ProductMetricsEntity(productId, metricsDate);
    }

    // 집계 증가 메소드
    public void increaseLikeCount() {
        this.likeCount++;
        updateLastLikedAt();
        updateLastUpdatedAt();
    }

    public void decreaseLikeCount() {
        if (this.likeCount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "좋아요 수는 0보다 작을 수 없습니다.");
        }
        this.likeCount--;
        updateLastUnLikedAt();
        updateLastUpdatedAt();
    }

    public void increaseClickCount() {
        this.clickCount++;
        updateLastClickedAt();
        updateLastUpdatedAt();
    }

    public void increaseViewCount() {
        this.viewCount++;
        updateLastViewedAt();
        updateLastUpdatedAt();
    }

    public void increaseSalesCount() {
        this.salesCount++;
        updateLastSoldAt();
        updateLastUpdatedAt();
    }

    // 일괄 집계 업데이트 메소드 (upsert용)
    public void updateMetrics(Long likeCount, Long clickCount, Long viewCount, Long salesCount) {
        this.likeCount = likeCount;
        this.clickCount = clickCount;
        this.viewCount = viewCount;
        this.salesCount = salesCount;
        updateLastUpdatedAt();
    }

    // 변화량 업데이트 메소드
    public void incrementMetrics(Long likeCountDelta, Long clickCountDelta, Long viewCountDelta, Long salesCountDelta) {
        if (likeCountDelta != null && likeCountDelta > 0) {
            this.likeCount += likeCountDelta;
            updateLastLikedAt();
        }
        if (clickCountDelta != null && clickCountDelta > 0) {
            this.clickCount += clickCountDelta;
            updateLastClickedAt();
        }
        if (viewCountDelta != null && viewCountDelta > 0) {
            this.viewCount += viewCountDelta;
            updateLastViewedAt();
        }
        if (salesCountDelta != null && salesCountDelta > 0) {
            this.salesCount += salesCountDelta;
            updateLastSoldAt();
        }
        updateLastUpdatedAt();
    }

    // 타임스탬프 업데이트 메소드
    private void updateLastSoldAt() {
        this.lastSoldAt = LocalDateTime.now();
    }

    private void updateLastViewedAt() {
        this.lastViewedAt = LocalDateTime.now();
    }

    private void updateLastLikedAt() {
        this.lastLikedAt = LocalDateTime.now();
    }

    private void updateLastUnLikedAt() {
        this.lastUnLikedAt = LocalDateTime.now();
    }

    private void updateLastClickedAt() {
        this.lastClickedAt = LocalDateTime.now();
    }

    private void updateLastUpdatedAt() {
        this.updatedAt = ZonedDateTime.now();
    }
}
