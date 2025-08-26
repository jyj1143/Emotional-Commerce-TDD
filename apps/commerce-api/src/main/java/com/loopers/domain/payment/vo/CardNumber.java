package com.loopers.domain.payment.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class CardNumber {

    private final String CARD_NO_PATTERN = "^\\d{4}-\\d{4}-\\d{4}-\\d{4}$";

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    public static CardNumber of(String cardNumber) {
        return new CardNumber(cardNumber);
    }

    private CardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            throw new IllegalArgumentException("카드 번호는 필수입니다.");
        }
        if (!cardNumber.matches(CARD_NO_PATTERN)) {
            throw new IllegalArgumentException("카드 번호는 16자리 숫자여야 합니다.");
        }
        this.cardNumber = cardNumber;
    }

}
