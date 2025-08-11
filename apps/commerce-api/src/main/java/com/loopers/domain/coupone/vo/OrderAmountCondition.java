package com.loopers.domain.coupone.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class OrderAmountCondition {

    @Column(nullable = false)
    private Integer minimumOrderAmount; // 최소 주문 금액

    @Column(nullable = false)
    private Integer maximumDiscountAmount; // 최대 할인 금액

    private OrderAmountCondition(Integer minimumOrderAmount, Integer maximumDiscountAmount) {
        if (minimumOrderAmount == null || minimumOrderAmount < 0)
            throw new IllegalArgumentException("최소 주문 금액은 0 이상이어야 합니다.");

        if (maximumDiscountAmount == null || maximumDiscountAmount < 0)
            throw new IllegalArgumentException("최대 할인 금액은 0 이상이어야 합니다.");

        this.minimumOrderAmount = minimumOrderAmount;
        this.maximumDiscountAmount = maximumDiscountAmount;
    }

    public static OrderAmountCondition of(Integer minimumOrderAmount, Integer maximumDiscountAmount) {
        return new OrderAmountCondition(minimumOrderAmount, maximumDiscountAmount);
    }

}
