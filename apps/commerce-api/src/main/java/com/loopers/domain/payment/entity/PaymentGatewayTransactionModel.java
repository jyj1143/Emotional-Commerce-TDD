package com.loopers.domain.payment.entity;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.common.vo.Money;
import com.loopers.domain.payment.enums.CardType;
import com.loopers.domain.payment.enums.PaymentStatus;
import com.loopers.domain.payment.vo.CardNumber;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

    @Column(name = "transaction_key")
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

    @Column(name = "reason")
    private String reason;

    private PaymentGatewayTransactionModel( Long refOrderId, Long refPaymentId, String transactionKey, PaymentStatus paymentStatus, Long amount, CardType cardType, String cardNumber) {
        this.refOrderId = refOrderId;
        this.refPaymentId = refPaymentId;
        this.transactionKey = transactionKey;
        this.paymentStatus = paymentStatus;
        this.amount = Money.of(amount);
        this.cardType = cardType;
        this.cardNumber = CardNumber.of(cardNumber);
    }

    public static PaymentGatewayTransactionModel of(Long refOrderId, Long refPaymentId, String transactionKey, PaymentStatus paymentStatus, Long amount, CardType cardType, String cardNumber) {
        return new PaymentGatewayTransactionModel(refOrderId, refPaymentId, transactionKey, paymentStatus, amount, cardType, cardNumber);
    }

    // 결제 완료 처리
    public void complete() {
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
    }

    // 결제 실패 처리
    public void fail(String reason) {
        this.paymentStatus = PaymentStatus.FAILED;
        this.paymentDate = LocalDateTime.now();
        this.reason = reason;
    }

    public void updateStatus(PaymentStatus paymentStatus){
        this.paymentStatus = paymentStatus;
    }

}
