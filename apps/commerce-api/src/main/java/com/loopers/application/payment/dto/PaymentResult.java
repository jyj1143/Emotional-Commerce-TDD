package com.loopers.application.payment.dto;

import com.loopers.domain.payment.enums.PaymentStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResult {

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Pay {
        private final Long paymentId;
        private final PaymentStatus paymentStatus;

        public static Pay from(PaymentResult.Pay result) {
            return Pay.builder()
                .paymentId(result.getPaymentId())
                .paymentStatus(result.getPaymentStatus())
                .build();
        }
    }
}
