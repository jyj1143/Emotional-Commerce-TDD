package com.loopers.domain.coupone.dto;


import com.loopers.domain.coupone.enums.CouponStatus;

public record CouponCommand (){

    public record IssueCoupon(
        Long couponPolicyId,
        Long userId
    ) {
    }

    public record UseCoupon(
        Long couponId,
        Long orderId,
        Long userId
    ) {
    }

    public record  GetMyCoupons(
        Integer page,
        Integer size,
        Long userId
    ){

    }

}
