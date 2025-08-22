package com.loopers.domain.payment.dto;

import com.loopers.domain.payment.entity.PaymentModel;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.enums.PaymentStatus;

public record PaymentInfo(

        Long id,
        Long userId,
        Long orderId,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        Long amount
) {

    public static PaymentInfo of(
            PaymentModel paymentModel
    ) {
        return new PaymentInfo(
                paymentModel.getId(),
                paymentModel.getRefUserId(),
                paymentModel.getRefOrderId(),
                paymentModel.getPaymentMethod(),
                paymentModel.getPaymentStatus(),
                paymentModel.getAmount().getAmount());
    }
}
