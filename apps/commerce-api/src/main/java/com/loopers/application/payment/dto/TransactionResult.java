package com.loopers.application.payment.dto;

import com.loopers.domain.payment.adapter.PaymentGatewayInfo;
import com.loopers.domain.payment.enums.PaymentStatus;

public record TransactionResult(
        String transactionKey,
        PaymentStatus status,
        String reason
) {
    public static TransactionResult from(PaymentGatewayInfo.Transaction info) {
        return new TransactionResult(
                info.transactionKey(),
                info.status(),
                info.reason()
        );
    }
}
