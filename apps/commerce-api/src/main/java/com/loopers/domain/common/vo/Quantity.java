package com.loopers.domain.common.vo;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Quantity {
    @Column(name = "quantity", nullable = false)
    private Long quantity; // 수량

    private Quantity(Long quantity) {
        if (quantity < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "수량은 0보다 작을 수 없습니다.");
        }
        this.quantity = quantity;
    }

    public static Quantity of(Long quantity) {
        return new Quantity(quantity);
    }

    public Quantity plus(Long other) {
        return new Quantity(this.quantity + other);
    }

    public Quantity minus(Long other) {
        return new Quantity(this.quantity - other);
    }
}
