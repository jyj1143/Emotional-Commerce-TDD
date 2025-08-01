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
public class Money {

    @Column(name = "amount", nullable = false)
    private Long amount; //  금액

    private Money(Long amount) {
        if (amount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "금액은 0보다 작을 수 없습니다.");
        }
        this.amount = amount;
    }

    public static Money of(Long amount) {
        return new Money(amount);
    }

    public Money plus(Long other) {
        return new Money(this.amount + other);
    }

    public Money minus(Long other) {
        return new Money(this.amount - other);
    }

    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }

}
