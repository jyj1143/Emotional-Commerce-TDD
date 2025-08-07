package com.loopers.domain.coupone;

public interface DiscountPolicy {
    Long calculateDiscount(Long originalPrice);
}

