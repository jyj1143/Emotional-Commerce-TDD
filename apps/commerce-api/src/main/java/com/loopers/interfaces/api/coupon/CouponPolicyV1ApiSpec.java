package com.loopers.interfaces.api.coupon;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.CreateRequest;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.CreateResponse;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.GetCouponPolicyResponse;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.SummaryListResponse;
import com.loopers.support.pagenation.PageResult;
import com.loopers.support.pagenation.Pageable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Coupon policy V1 API", description = "Coupon policy management API for version 1")
public interface CouponPolicyV1ApiSpec {

    @Operation(
        summary = "쿠폰 정책을 생성",
        description = "새로운 쿠폰 정책을 생성합니다."
    )
    ApiResponse<CreateResponse> createCouponPolicyRequest(
        CreateRequest request
    );


    @Operation(
        summary = "쿠폰 정책 조회",
        description = "특정 쿠폰 정책의 상세 정보를 조회합니다."
    )
    ApiResponse<GetCouponPolicyResponse> getCouponPolicy(Long couponId);

    @Operation(
        summary = "쿠폰 정책 목록 조회",
        description = "쿠폰 정책 목록을 조회합니다."
    )
    ApiResponse<PageResult<SummaryListResponse>> getCouponPolicies(Pageable pageable);

}
