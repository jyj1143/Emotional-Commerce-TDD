package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.dto.PaymentCriteria;
import com.loopers.application.payment.dto.TransactionResult;
import com.loopers.domain.payment.enums.CardType;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.enums.TransactionStatus;

public class PaymentV1Dto {

    public record TransactionResponse(
            String transactionKey,
            PaymentStatusDto status,
            String reason
    ) {
        public static TransactionResponse from(TransactionResult result){
            return new TransactionResponse(
                result.transactionKey(),
                PaymentStatusDto.valueOf(result.status().name()),
                result.reason()
            );
        }


    }

    public record PaymentRequest(
            Long orderId,
            CardTypeDto cardType,
            String cardNo,
            Long amount,
            PaymentMethod paymentMethod
    ) {
        public PaymentCriteria.PgPay toPaymentCriteria() {
            return new PaymentCriteria.PgPay(
                orderId,
                cardType.toCardType(),
                cardNo,
                amount,
                paymentMethod
            );
        }
    }

    public record CallbackRequest(
            String transactionKey,
            Long orderId,
            CardTypeDto cardType,
            String cardNo,
            Long amount,
            TransactionStatusDto status,
            String reason
    ){}


    public enum CardTypeDto {
        SAMSUNG,
        KB,
        HYUNDAI,
        ;

        public CardType toCardType() {
            return switch (this) {
                case SAMSUNG -> CardType.SAMSUNG;
                case KB -> CardType.KB;
                case HYUNDAI -> CardType.HYUNDAI;
            };
        }
    }

    public enum TransactionStatusDto{
        PENDING,
        SUCCESS,
        FAILED
        ;
        public TransactionStatus toTransactionStatus() {
            return switch (this) {
                case PENDING -> TransactionStatus.PENDING;
                case SUCCESS -> TransactionStatus.SUCCESS;
                case FAILED -> TransactionStatus.FAILED;
            };
        }
    }

    public enum PaymentStatusDto {
        PENDING, // 결제 대기 중
        COMPLETED, // 결제 완료
        FAILED, // 결제 실패
        CANCELLED // 결제 취소(환불)
        ;
    }

}
