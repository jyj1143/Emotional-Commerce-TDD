package com.loopers.interfaces.event.payment;


import com.loopers.domain.order.dto.OrderEvent;
import com.loopers.domain.order.dto.OrderEvent.Created.OrderItem;
import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.service.PaymentService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@MockitoSettings
class PaymentEventListenerTest {

    @InjectMocks
    private PaymentEventListener sut;

    @Mock
    private PaymentService paymentService;

    @DisplayName("주문 생성 이벤트가 발생하면,")
    @Nested
    class OrderCreated {

        @DisplayName("결제 준비를 한다.")
        @Test
        void readyPayment() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            Long couponId = 1L;
            List<OrderItem> items = List.of(
                new OrderItem(1L, orderId, 2L, 10000L, 1L),
                new OrderItem(2L, orderId, 1L, 20000L, 2L)
            );
            Long finalPrice = 40000L;
            OrderEvent.Created event = new OrderEvent.Created(orderId, userId, couponId, items, finalPrice);

            // when
            sut.handleOrderCreatedEvent(event);

            // then
            verify(paymentService, times(1)).ready(any(PaymentCommand.Ready.class));

        }

    }

}
