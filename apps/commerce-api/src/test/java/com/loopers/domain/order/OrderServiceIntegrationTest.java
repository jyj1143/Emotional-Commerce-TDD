package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.domain.order.dto.OrderCommand;
import com.loopers.domain.order.dto.OrderCommand.Order.OrderItem;
import com.loopers.domain.order.dto.OrderInfo;
import com.loopers.domain.order.enums.OrderStatus;
import com.loopers.domain.order.service.OrderService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @DisplayName("주문을 생성할 때, ")
    @Nested
    class Order {
        @DisplayName("정상적인 회원 ID와 주문 아이템을 입력하면 주문 생성을 성공한다.")
        @Test
        void when_userIdAndOrderItemsGiven_then_createOrder() {
            // given
            OrderCommand.Order command = new OrderCommand.Order(
                1L,
                List.of(new OrderItem(10L, 10L, 10L))
            );

            // when
            OrderInfo saved = orderService.placeOrder(command);

            // then
            assertAll(
                () -> assertThat(saved.id()).isNotNull(),
                () -> assertThat(saved.userId()).isEqualTo(command.userId()),
                () -> assertThat(saved.status()).isEqualTo(OrderStatus.PENDING)
            );
        }

    }
}
