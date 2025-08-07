package com.loopers.domain.coupone;

import com.loopers.domain.coupone.entity.CouponPolicyModel;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageDiscount implements DiscountPolicy {
    private final BigDecimal discountRate;
    private final Integer maximumDiscountAmount;

    public PercentageDiscount(CouponPolicyModel policy) {
        this.discountRate = policy.getDiscountValue().getDiscountValue();
        this.maximumDiscountAmount = policy.getOrderAmountCondition().getMaximumDiscountAmount();
    }

    @Override
    public Long calculateDiscount(Long originalPrice) {
        BigDecimal discount = BigDecimal.valueOf(originalPrice)
            .multiply(discountRate)
            .divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN);

        Long calculatedDiscount = discount.longValue();
        return maximumDiscountAmount != null ?
            Math.min(calculatedDiscount, maximumDiscountAmount) : calculatedDiscount;
    }

}
