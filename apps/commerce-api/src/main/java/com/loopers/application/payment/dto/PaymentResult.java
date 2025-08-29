package com.loopers.application.payment.dto;

import com.loopers.domain.payment.dto.PaymentInfo;
import com.loopers.domain.payment.enums.PaymentStatus;

public record PaymentResult (
        Long paymentId,
        PaymentStatus paymentStatus,
        String transactionKey,
        String reason
){
    public static PaymentResult from(PaymentInfo paymentInfo) {
        return new PaymentResult(paymentInfo.id(), paymentInfo.paymentStatus());
    }
}
