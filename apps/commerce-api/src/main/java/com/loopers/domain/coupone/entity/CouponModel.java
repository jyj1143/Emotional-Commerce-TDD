package com.loopers.domain.coupone.entity;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.coupone.enums.CouponStatus;
import com.loopers.domain.coupone.vo.UsagePeriod;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "coupon")
public class CouponModel extends BaseEntity {

    @Column(name = "ref_coupon_policy_id", nullable = false)
    private Long refCouponPolicyId;

    @Column(name = "ref_user_id", nullable = false)
    private Long refUserId;

    @Column(name = "ref_order_id")
    private Long orderId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    @Embedded
    private UsagePeriod usagePeriod;

    @Column(name = "used_at")
    private LocalDateTime usedAt;


    private CouponModel(Long refCouponPolicyId, Long refUserId, CouponStatus couponStatus, LocalDateTime issuedAt,
        LocalDateTime expirationTime) {
        if (refCouponPolicyId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 ID는 필수 값입니다.");
        }

        if (refUserId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "회원 ID는 필수 값입니다.");
        }

        if (couponStatus == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 상태는 필수 값입니다.");
        }

        this.couponStatus = couponStatus;
        this.refCouponPolicyId = refCouponPolicyId;
        this.refUserId = refUserId;
        this.usagePeriod = UsagePeriod.of(issuedAt, expirationTime);
    }

    public static CouponModel of(Long refCouponPolicyId, Long refUserId, CouponStatus couponStatus, LocalDateTime issuedAt,
        LocalDateTime expirationTime) {
        return new CouponModel(refCouponPolicyId, refUserId, couponStatus, issuedAt, expirationTime);
    }

    public boolean isUsed() {
        return couponStatus == CouponStatus.USED;
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(usagePeriod.getExpirationTime());
    }

    public void use(Long orderId) {
        if (isUsed()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이미 사용한 쿠폰입니다.");
        }
        if (isExpired()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "만료된 쿠폰입니다.");
        }
        this.couponStatus = CouponStatus.USED;
        this.orderId = orderId;
        this.usedAt = LocalDateTime.now();
    }

}
