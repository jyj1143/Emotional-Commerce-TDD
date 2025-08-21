package com.loopers.application.payment;

import com.loopers.application.payment.dto.PaymentCriteria;
import com.loopers.application.payment.dto.TransactionResult;
import com.loopers.domain.payment.adapter.PaymentGatewayAdapter;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo;
import com.loopers.domain.payment.dto.PaymentCommand.ReadyTransaction;
import com.loopers.domain.payment.dto.PaymentInfo;
import com.loopers.domain.payment.entity.PaymentGatewayTransactionModel;
import com.loopers.domain.payment.enums.PaymentStatus;
import com.loopers.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final PaymentGatewayAdapter paymentGatewayAdapter;

    public TransactionResult processPayment(PaymentCriteria.PgPay criteria) {

        // 결제 정보 조회
        PaymentInfo paymentInfo = paymentService.findByRefOrderId(criteria.orderId());

        // 결제 게이트웨이 트랜잭션 준비
        paymentService.readyPaymentGatewayTransaction(
            new ReadyTransaction(
                paymentInfo.id(),
                paymentInfo.orderId(),
                null, // 트랜잭션 키는 결제 게이트웨이에서 생성됨
                paymentInfo.paymentStatus(),
                paymentInfo.amount(),
                criteria.cardType(),
                criteria.cardNo()
            )
        );

        // 결제 게이트웨이에 결제 요청
        PaymentGatewayInfo.Transaction gatewayResponse =
            paymentGatewayAdapter.processPayment(criteria.toPaymentCommand());

        // 결제가 성공한 경우에만 완료 처리
        if (gatewayResponse != null && PaymentStatus.COMPLETED.name().equals(gatewayResponse.status().name())) {
            // 트랜잭션 완료 처리
            paymentService.completePayment(gatewayResponse.transactionKey());
        }

        return TransactionResult.from(gatewayResponse);
    }

}
