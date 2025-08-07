package com.loopers.domain.coupone;

import com.loopers.domain.coupone.entity.CouponPolicyModel;

public class FixedAmountDiscount implements DiscountPolicy {

    private final Long discountAmount;
    private final Integer maximumDiscountAmount;

    public FixedAmountDiscount(CouponPolicyModel policy) {
        this.discountAmount = policy.getDiscountValue().getDiscountValue().longValue();
        this.maximumDiscountAmount = policy.getOrderAmountCondition().getMaximumDiscountAmount();
    }

    @Override
    public Long calculateDiscount(Long originalPrice) {
        return Math.min(discountAmount,
            maximumDiscountAmount != null ? maximumDiscountAmount : discountAmount);
    }
}

