package com.loopers.domain.productMetrics.entity;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "product_metrics")
public class ProductMetricsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_product_id", nullable = false)
    private Long productId;

    /**
     * 집계 관련 필드
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


    private ProductMetricsEntity(Long productId) {
        this.productId = productId;
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
    }

    public static ProductMetricsEntity create(Long productId){
        return new ProductMetricsEntity(productId);
    }

    // 집계 증가 메소드
    public void increaseLikeCount() {
        this.likeCount++;
        updateLastLikedAt();
    }

    public void decreaseLikeCount() {
        if (this.likeCount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "좋아요 수는 0보다 작을 수 없습니다.");
        }
        this.likeCount--;
        updateLastUnLikedAt();
    }

    public void increaseClickCount() {
        this.clickCount++;
        updateLastClickedAt();
    }

    public void increaseViewCount() {
        this.viewCount++;
        updateLastViewedAt();
    }

    public void increaseSalesCount() {
        this.salesCount++;
        updateLastSoldAt();
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

}
