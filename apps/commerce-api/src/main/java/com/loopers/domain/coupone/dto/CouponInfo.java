package com.loopers.domain.coupone.dto;

import com.loopers.domain.coupone.entity.CouponModel;
import com.loopers.domain.coupone.enums.CouponStatus;
import java.time.LocalDateTime;

public record CouponInfo (
    Long id,
    Long refCouponPolicyId,
    Long refUserId,
    Long orderId,
    CouponStatus couponStatus,
    LocalDateTime issuedAt,
    LocalDateTime expirationTime

){
    public static CouponInfo from(
        CouponModel coupon
    ){
        return new CouponInfo(
            coupon.getId(),
            coupon.getRefCouponPolicyId(),
            coupon.getRefUserId(),
            coupon.getOrderId(),
            coupon.getCouponStatus(),
            coupon.getUsagePeriod().getIssuedAt(),
            coupon.getUsagePeriod().getExpirationTime()
        );
    }
}
