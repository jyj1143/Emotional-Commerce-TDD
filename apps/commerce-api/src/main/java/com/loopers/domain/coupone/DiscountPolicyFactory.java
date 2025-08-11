package com.loopers.domain.coupone;


import com.loopers.domain.coupone.entity.CouponPolicyModel;

public class DiscountPolicyFactory {
    public static DiscountPolicy createFrom(CouponPolicyModel policy) {
        return switch (policy.getDiscountType()) {
            case FIXED_AMOUNT -> new FixedAmountDiscount(policy);
            case PERCENTAGE -> new PercentageDiscount(policy);
        };
    }
}
