package com.loopers.interfaces.event.payment;

import com.loopers.domain.order.dto.OrderEvent;
import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final PaymentService paymentService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCreatedEvent(OrderEvent.Created event) {
        // 결제 대기
        paymentService.ready(new PaymentCommand.Ready(event.userId(), event.orderId(), event.finalPrice()));
    }
}
