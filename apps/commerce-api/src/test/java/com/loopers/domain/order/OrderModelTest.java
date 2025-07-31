package com.loopers.domain.order;


import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.loopers.domain.order.enums.OrderStatus;
import com.loopers.support.error.CoreException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderModelTest {

    @DisplayName("주문 모델을 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("회원 ID가 비어있으면, CoreException 예외가 발생한다.")
        @Test
        void when_userIdNull_then_throwCoreException() {
        // given
            Long userId = null;
            List<OrderItemModel> items = List.of(OrderItemModel.of(1L, 1L, 1L));
            // when
            // then
             assertThrows(CoreException.class, () -> {
                 OrderModel.of(
                     null,
                     items,
                     OrderStatus.PENDING);
             });
        }

        @DisplayName("주문 아이템이 비어있으면, CoreException 예외가 발생한다.")
        @Test
        void when_orderItemsEmpty_then_throwCoreException() {
            // given
            Long userId = 1L;
            List<OrderItemModel> items = List.of();
            // when
            // then
            assertThrows(CoreException.class, () -> {
                OrderModel.of(
                    userId,
                    items,
                    OrderStatus.PENDING);
            });
        }

        @DisplayName("주문상태가 비어있으면, CoreException 예외가 발생한다.")
        @Test
        void when_orderStatusNull_then_throwCoreException() {
            // given
            Long userId = 1L;
            List<OrderItemModel> items = List.of(OrderItemModel.of(1L, 1L, 1L));
            // when
            // then
            assertThrows(CoreException.class, () -> {
                OrderModel.of(
                    userId,
                    items,
                    null);
            });
        }


        @DisplayName("정상적이 회원 ID, 주문 아이템, 주문 상태를 입력하면 주문 생성을 성공한다.")
        @Test
        void when_orderValid_then_createOrder() {
            // given
            Long userId = 1L;
            List<OrderItemModel> items = List.of(OrderItemModel.of(1L, 1000L, 1L));
            OrderStatus orderStatus = OrderStatus.PENDING;

            // when
            OrderModel orderModel = OrderModel.of(
                userId,
                items,
                orderStatus);

            // then
            assertNotNull(orderModel);
            assertEquals(userId, orderModel.getRefUserId());
            assertEquals(items.size(), orderModel.getOrderItemModels().size());
            assertEquals(orderStatus, orderModel.getStatus());
        }
    }

}
