package com.loopers.application.payment.strategy;

import com.loopers.application.payment.dto.PaymentResult;
import com.loopers.application.payment.dto.TransactionResult;
import com.loopers.application.payment.strategy.condition.CardPaymentCondition;
import com.loopers.domain.payment.adapter.PaymentGatewayAdapter;
import com.loopers.domain.payment.adapter.PaymentGatewayCommand.Payment;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo;
import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.dto.PaymentCommand.ReadyTransaction;
import com.loopers.domain.payment.dto.PaymentInfo;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.enums.PaymentStatus;
import com.loopers.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardPaymentStrategy implements PaymentStrategy<CardPaymentCondition> {

    private final PaymentService paymentService;
    private final PaymentGatewayAdapter paymentGatewayAdapter;

    @Override
    public PaymentResult pay(CardPaymentCondition condition) {
        PaymentInfo paymentInfo = paymentService.findByRefOrderId(condition.getOrderId());
        PaymentGatewayInfo.Transaction gatewayResponse =
            paymentGatewayAdapter.processPayment(new Payment(
                condition.getOrderId(),
                condition.getCardType(),
                condition.getCardNo(),
                condition.getAmount()
            ));

        paymentService.readyPaymentGatewayTransaction(
            new ReadyTransaction(
                paymentInfo.id(),
                paymentInfo.orderId(),
                gatewayResponse.transactionKey(), // 트랜잭션 키는 결제 게이트웨이에서 생성됨
                paymentInfo.paymentStatus(),
                paymentInfo.amount(),
                condition.getCardType(),
                condition.getCardNo()
            )
        );

        // 결제 준비
        paymentService.pay(new PaymentCommand.Pay(condition.getUserId(),condition.getOrderId(), PaymentMethod.CARD, condition.getAmount()));
        return null;
    }
}

