package com.loopers.application.payment.dto;

import com.loopers.domain.payment.enums.PaymentStatus;

public record TransactionResult(
        String transactionKey,
        PaymentStatus status,
        String reason
) {

}
