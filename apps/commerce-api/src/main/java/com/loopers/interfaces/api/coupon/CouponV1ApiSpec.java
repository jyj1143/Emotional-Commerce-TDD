package com.loopers.interfaces.api.coupon;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.CreateRequest;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.CreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Coupon V1 API", description = "Coupon management API for version 1")
public interface CouponV1ApiSpec {

    @Operation(
        summary = "쿠폰 정책을 생성",
        description = "새로운 쿠폰 정책을 생성합니다."
    )
    ApiResponse<CreateResponse> createCouponPolicyRequest(
        CreateRequest request
    );

}
