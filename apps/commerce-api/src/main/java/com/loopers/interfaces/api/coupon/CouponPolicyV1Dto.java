package com.loopers.interfaces.api.coupon;

public record CouponPolicyV1Dto() {

    // 생성 요청
    public record CreateRequest(

    ) {

    }

    // 생성 응답
    public record CreateResponse(

    ) {

    }

    // 단건 조회 응답
    public record GetCouponPolicyResponse(

    ) {

    }

    // 다건 조회 응답
    public record SummaryListResponse(

    ) {

    }

}
