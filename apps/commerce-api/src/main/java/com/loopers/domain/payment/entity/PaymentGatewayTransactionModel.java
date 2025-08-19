package com.loopers.domain.payment.entity;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.common.vo.Money;
import com.loopers.domain.payment.enums.CardType;
import com.loopers.domain.payment.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "payment_gateway_transaction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentGatewayTransactionModel extends BaseEntity {

    @Column(name = "ref_order_id", nullable = false)
    private Long refOrderId; // 주문 ID

    @Column(name = "ref_payment_id", nullable = false)
    private Long refPaymentId; // 결제 ID

    @Column(name = "transaction_key", nullable = false)
    private String transactionKey;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "amount", nullable = false)
    private Money amount; // 결제 금액

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(name = "payment_date", nullable = false)
    LocalDateTime paymentDate = LocalDateTime.now(); // 결제 일시

    private PaymentGatewayTransactionModel(Long refOrderId, Long refPaymentId, String transactionKey,
        PaymentStatus paymentStatus, Long amount, CardType cardType, LocalDateTime paymentDate) {
        this.refOrderId = refOrderId;
        this.refPaymentId = refPaymentId;
        this.transactionKey = transactionKey;
        this.paymentStatus = paymentStatus;
        this.amount = Money.of(amount);
        this.cardType = cardType;
    }

    public static PaymentGatewayTransactionModel of(Long refOrderId, Long refPaymentId, String transactionKey,
        PaymentStatus paymentStatus, Long amount, CardType cardType, LocalDateTime paymentDate) {
        return new PaymentGatewayTransactionModel(refOrderId, refPaymentId, transactionKey, paymentStatus, amount, cardType,
            paymentDate);
    }
}
