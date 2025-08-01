package com.loopers.domain.product.vo;

import com.loopers.domain.common.vo.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Price {

    @AttributeOverride(name = "amount", column = @Column(name = "sale_price", nullable = false))
    Money salePrice;

    private Price(Long price) {
        if (price == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "가격은 비어있을 수 없습니다.");
        }
        if (price < 0L) {
            throw new CoreException(ErrorType.BAD_REQUEST, "가격은 음수일 수 없습니다.");
        }
        this.salePrice = Money.of(price);
    }

    public static Price of(Long price) {
        return new Price(price);
    }


    public int compareTo(Price other) {
        return this.salePrice.compareTo(other.salePrice);
    }
}
