package com.loopers.domain.coupone.dto;

public record CouponCommand (){

    public record Apply(
        Long couponId,
        Long orderId,
        Long userId,
        Long totalPrice
    ){

    }

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
