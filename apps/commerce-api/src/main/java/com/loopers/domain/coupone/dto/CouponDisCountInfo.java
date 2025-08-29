package com.loopers.domain.coupone.dto;


public record CouponDisCountInfo(
    Long couponId,
    Long discountApplyPrice
){
    public static CouponDisCountInfo from(
        Long couponId,
        Long discountApplyPrice
    ){
        return new CouponDisCountInfo(
            couponId,
            discountApplyPrice
        );
    }
}
