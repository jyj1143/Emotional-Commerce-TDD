package com.loopers.domain.point.vo;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Amount {
    @Column(name = "amount", nullable = false)
    Long amount;

    public Amount(Long amount) {
        if (amount == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트는 비어있을 수 없습니다.");
        }
        if (amount < 0L) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트는 음수일 수 없습니다.");
        }
        this.amount = amount;
    }
}
