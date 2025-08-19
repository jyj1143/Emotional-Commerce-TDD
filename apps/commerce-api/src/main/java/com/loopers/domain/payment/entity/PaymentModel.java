package com.loopers.domain.payment.entity;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.common.vo.Money;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.enums.PaymentStatus;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentModel extends BaseEntity {

    @Column(name = "ref_order_id", nullable = false)
    private Long refOrderId; // 주문 ID

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "amount", nullable = false)
    private Money amount; // 결제 금액

    private PaymentModel(Long refOrderId, PaymentMethod paymentMethod, PaymentStatus paymentStatus, Long amount) {
        if (refOrderId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 ID는 필수 값입니다.");
        }
        if (paymentMethod == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 방법은 필수 값입니다.");
        }
        if (paymentStatus == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 상태는 필수 값입니다.");
        }

        this.refOrderId = refOrderId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.amount = Money.of(amount);
    }

    public static PaymentModel of(Long refOrderId, PaymentMethod paymentMethod, PaymentStatus paymentStatus, Long amount) {
        return new PaymentModel(refOrderId, paymentMethod, paymentStatus, amount);
    }

    public void complete(){
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public void fail() {
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public boolean isPending() {
        return this.paymentStatus == PaymentStatus.PENDING;
    }
}
