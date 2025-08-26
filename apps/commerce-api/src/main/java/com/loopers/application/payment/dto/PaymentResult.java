package com.loopers.application.payment.dto;

import com.loopers.domain.payment.enums.PaymentStatus;

public record PaymentResult (
        Long paymentId,
        PaymentStatus paymentStatus
){

}
