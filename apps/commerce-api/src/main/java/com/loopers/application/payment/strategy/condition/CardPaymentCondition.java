package com.loopers.application.payment.strategy.condition;

import com.loopers.domain.payment.enums.CardType;
import lombok.Getter;

// 카드 결제에 필요한 조건
@Getter
public class CardPaymentCondition extends PaymentCondition {

    private final Long userId;
    private final CardType cardType;
    private final String cardNo;

    public CardPaymentCondition(Long orderId, Long amount, Long userId, CardType cardType, String cardNo) {
        super(orderId, amount);
        this.userId = userId;
        this.cardType = cardType;
        this.cardNo = cardNo;
    }
}
