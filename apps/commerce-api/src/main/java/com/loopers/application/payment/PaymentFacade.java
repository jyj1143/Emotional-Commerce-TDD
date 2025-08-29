package com.loopers.application.payment;

import com.loopers.application.payment.dto.PaymentCriteria;
import com.loopers.application.payment.dto.PaymentResult;
import com.loopers.application.payment.dto.TransactionResult;
import com.loopers.application.payment.strategy.CardPaymentStrategy;
import com.loopers.application.payment.strategy.PaymentContext;
import com.loopers.application.payment.strategy.PointPaymentStrategy;
import com.loopers.application.payment.strategy.condition.CardPaymentCondition;
import com.loopers.application.payment.strategy.condition.PointPaymentCondition;
import com.loopers.domain.payment.adapter.PaymentGatewayAdapter;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo;
import com.loopers.domain.payment.dto.PaymentCommand.ReadyTransaction;
import com.loopers.domain.payment.dto.PaymentInfo;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.enums.PaymentStatus;
import com.loopers.domain.payment.service.PaymentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final PaymentGatewayAdapter paymentGatewayAdapter;
    private final PaymentContext paymentContext;
    private final CardPaymentStrategy cardPaymentStrategy;
    private final PointPaymentStrategy pointPaymentStrategy;

    @PostConstruct
    public void initStrategies() {
        // 전략 미리 등록
        paymentContext.registerStrategy(PaymentMethod.CARD, cardPaymentStrategy);
        paymentContext.registerStrategy(PaymentMethod.POINT, pointPaymentStrategy);
    }

    public TransactionResult processPayment(PaymentCriteria.PgPay criteria) {
        PaymentInfo paymentInfo = paymentService.findByRefOrderId(criteria.orderId());
        PaymentResult paymentResult = executePaymentStrategy(paymentInfo, criteria);
        return new TransactionResult(paymentResult.transactionKey(), paymentResult.paymentStatus(), paymentResult.reason());
    }

    private PaymentResult executePaymentStrategy(PaymentInfo paymentInfo, PaymentCriteria.PgPay criteria) {
        switch (paymentInfo.paymentMethod()) {
            case CARD -> {
                CardPaymentCondition condition = new CardPaymentCondition(
                        paymentInfo.userId(),
                        paymentInfo.orderId(),
                        paymentInfo.amount(),
                        criteria.cardType(),
                        criteria.cardNo()
                );
                return paymentContext.executePay(PaymentMethod.CARD, condition);
            }
            case POINT -> {
                PointPaymentCondition condition = new PointPaymentCondition(
                        paymentInfo.userId(),
                        paymentInfo.orderId(),
                        paymentInfo.amount()
                );
                return paymentContext.executePay(PaymentMethod.POINT, condition);
            }
            default -> throw new IllegalArgumentException("지원하지 않는 결제 방법입니다: " + paymentInfo.paymentMethod());
        }
    }

    public void concludePayment(PaymentCriteria.Conclude criteria) {
        switch (criteria.status()){
            case SUCCESS -> paymentService.success(criteria.toSuccessCommand());
            case FAILED -> paymentService.fail(criteria.toFailCommand());
        }
    }

}
