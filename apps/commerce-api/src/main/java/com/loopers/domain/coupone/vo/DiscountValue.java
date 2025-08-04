package com.loopers.domain.coupone.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class DiscountValue {

    @Column(nullable = false)
    private BigDecimal discountValue; // 할인 값/률

    private DiscountValue(BigDecimal discountValue) {
        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("할인 값은 0 이상이어야 합니다.");
        }
        this.discountValue = discountValue.setScale(2, RoundingMode.HALF_UP); // 소수점 2자리 고정
    }

    public static DiscountValue of(BigDecimal discountValue) {
        return new DiscountValue(discountValue);
    }

}
