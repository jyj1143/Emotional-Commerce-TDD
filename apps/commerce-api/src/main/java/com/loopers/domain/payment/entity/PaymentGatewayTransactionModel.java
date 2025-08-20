package com.loopers.domain.payment.entity;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.common.vo.Money;
import com.loopers.domain.payment.enums.CardType;
import com.loopers.domain.payment.enums.PaymentStatus;
import com.loopers.domain.payment.vo.CardNumber;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "payment_gateway_transaction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentGatewayTransactionModel extends BaseEntity {

    @Column(name = "store_id", nullable = false, updatable = false)
    private Long storeId; // 파트너사 ID

    @Column(name = "ref_order_id", nullable = false)
    private Long refOrderId; // 주문 ID

    @Column(name = "ref_payment_id", nullable = false)
    private Long refPaymentId; // 결제 ID

    @Column(name = "transaction_key", nullable = false)
    private String transactionKey;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Embedded
    private Money amount; // 결제 금액

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Embedded
    private CardNumber cardNumber;

    @Column(name = "payment_date", nullable = false)
    LocalDateTime paymentDate = LocalDateTime.now(); // 결제 일시

    @Builder
    public PaymentGatewayTransactionModel(Long storeId, Long refOrderId, Long refPaymentId,
        String transactionKey, PaymentStatus paymentStatus, Money amount, CardType cardType,
        CardNumber cardNumber, LocalDateTime paymentDate) {
        this.storeId = storeId;
        this.refOrderId = refOrderId;
        this.refPaymentId = refPaymentId;
        this.transactionKey = transactionKey;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.cardType = cardType;
        this.cardNumber = cardNumber;
        this.paymentDate = paymentDate;
    }
}
