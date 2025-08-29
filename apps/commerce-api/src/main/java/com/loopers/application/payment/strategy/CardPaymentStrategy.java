
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
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardPaymentStrategy implements PaymentStrategy<CardPaymentCondition> {

    private final PaymentService paymentService;
    private final PaymentGatewayAdapter paymentGatewayAdapter;

    @Override
    public PaymentResult pay(CardPaymentCondition condition) {
        PaymentInfo paymentInfo = paymentService.findByRefOrderId(condition.getOrderId());

        // 결제 게이트웨이 호출
        PaymentGatewayInfo.Transaction gatewayResponse =
                paymentGatewayAdapter.processPayment(new Payment(
                        condition.getOrderId(),
                        condition.getCardType(),
                        condition.getCardNo(),
                        condition.getAmount()
                ));

        // 게이트웨이 트랜잭션 준비
        paymentService.readyPaymentGatewayTransaction(
                new ReadyTransaction(
                        paymentInfo.id(),
                        paymentInfo.orderId(),
                        gatewayResponse.transactionKey(),
                        paymentInfo.paymentStatus(),
                        paymentInfo.amount(),
                        condition.getCardType(),
                        condition.getCardNo()
                )
        );

        // 결제 상태에 따른 처리
        if (gatewayResponse.isSuccess()) {
            // 성공 처리
            paymentService.pay(new PaymentCommand.Pay(
                    condition.getUserId(),
                    condition.getOrderId(),
                    PaymentMethod.CARD,
                    condition.getAmount()
            ));

            PaymentInfo success = paymentService.success(new PaymentCommand.Success(
                    condition.getOrderId(),
                    gatewayResponse.transactionKey()
            ));

            log.info("카드 결제 성공 - 주문ID: {}, 트랜잭션키: {}",
                    condition.getOrderId(), gatewayResponse.transactionKey());

            return new PaymentResult(success.id(), success.paymentStatus(), gatewayResponse.transactionKey(), null);
        } else {
            // 실패 처리
            PaymentInfo fail = paymentService.fail(new PaymentCommand.Fail(
                    condition.getOrderId(),
                    gatewayResponse.transactionKey(),
                    gatewayResponse.reason()
            ));

            log.warn("카드 결제 실패 - 주문ID: {}, 사유: {}",
                    condition.getOrderId(), gatewayResponse.reason());

            return new PaymentResult(fail.id(), fail.paymentStatus(), gatewayResponse.transactionKey(), gatewayResponse.reason());
        }
    }
}
