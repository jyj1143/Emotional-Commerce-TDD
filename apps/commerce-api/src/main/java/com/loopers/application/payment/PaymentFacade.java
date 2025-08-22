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
            paymentService.completePayment(criteria.orderId());

            // PaymentMethod에 따라 적절한 결제 전략 실행
           executePaymentStrategy(paymentInfo, criteria);
        }

        return TransactionResult.from(gatewayResponse);
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

}
