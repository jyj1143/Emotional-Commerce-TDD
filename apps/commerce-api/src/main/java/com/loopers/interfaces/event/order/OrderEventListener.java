package com.loopers.interfaces.event.order;

import com.loopers.domain.order.dto.OrderCommand;
import com.loopers.domain.order.service.OrderEventPublisher;
import com.loopers.domain.order.service.OrderService;
import com.loopers.domain.payment.dto.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final OrderService orderService;
    private final OrderEventPublisher orderEventPublisher;

    /**
     * 결제 성공 이벤트 처리
     * - 주문 완료 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePaymentSucceededEvent(PaymentEvent.PaymentSucceeded event) {
        log.info("결제 성공 이벤트 수신 - 주문 ID: {}, 결제 ID: {}", event.orderId(), event.paymentId());

        // 주문 완료 처리
        orderService.completeOrder(new OrderCommand.Complete(event.orderId(), event.userId(), event.amount()));
    }

    /**
     * 결제 실패 이벤트 처리
     * - 주문 취소 처리
     * - 재고 및 쿠폰 복원
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePaymentFailedEvent(PaymentEvent.PaymentFailed event) {
        log.info("결제 실패 이벤트 수신 - 주문 ID: {}, 결제 ID: {}, 사유: {}",
            event.orderId(), event.paymentId(), event.reason());

        // 주문 취소 처리
        orderService.cancelOrder(new OrderCommand.Cancel(event.orderId(), event.userId(), event.amount()));
    }


}
