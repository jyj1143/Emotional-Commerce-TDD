package com.loopers.interfaces.api.coupon;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.CreateRequest;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.CreateResponse;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.GetCouponPolicyResponse;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.SummaryListResponse;
import com.loopers.support.pagenation.PageResult;
import com.loopers.support.pagenation.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/coupon-policy")
public class CouponPolicyV1ApiController implements CouponPolicyV1ApiSpec {


    @PostMapping()
    @Override
    public ApiResponse<CreateResponse> createCouponPolicyRequest(
        CreateRequest request) {
        return null;
    }

    @GetMapping("/{couponId}")
    @Override
    public ApiResponse<GetCouponPolicyResponse> getCouponPolicy(Long couponId) {
        return null;
    }

    @GetMapping()
    @Override
    public ApiResponse<PageResult<SummaryListResponse>> getCouponPolicies(
         Pageable pageable) {
        return null;
    }
}
