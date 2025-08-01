package com.loopers.domain.order;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import com.loopers.domain.order.enums.OrderStatus;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderItemModelTest {

    @DisplayName("주문 아이템 모들을 생성할 때, ")
    @Nested
    class Create {

        @DisplayName("주문 아이템의 수량이 음수이면, CoreException 예외가 발생한다.")
        @Test
        void when_orderItemQuantityNegative_then_throwCoreException() {
            // when
            // then
            assertThrows(CoreException.class, () -> {
                OrderItemModel.of(-1L, 1L, 1L);
            });
        }

        @DisplayName("주문 아이템의 구매가격이 음수이면, CoreException 예외가 발생한다.")
        @Test
        void when_orderItemPurchasePriceNegative_then_throwCoreException() {
            // when
            // then
            assertThrows(CoreException.class, () -> {
                OrderItemModel.of(1L, -1000L, 1L);
            });
        }

        @DisplayName("주문 아이템의 sku ID가 null이면, CoreException 예외가 발생한다.")
        @Test
        void when_orderItemSkuIdNull_then_throwCoreException() {
            // when
            // then
            assertThrows(CoreException.class, () -> {
                OrderItemModel.of(1L, 1000L, null);
            });
        }

        @DisplayName("주문 아이템을 정상적으로 생성하면 성공한다.")
        @Test
        void when_orderItemValid_then_createOrderItem() {
            // given
            Long quantity = 1L;
            Long purchasePrice = 1000L;
            Long skuId = 1L;

            // when
            OrderItemModel orderItemModel = OrderItemModel.of(quantity, purchasePrice, skuId);

            // then
            assertNotNull(orderItemModel);
            assertEquals(quantity, orderItemModel.getQuantity().getQuantity());
            assertEquals(purchasePrice, orderItemModel.getPurchasePrice().getAmount());
            assertEquals(skuId, orderItemModel.getRefProductSkuId());
        }

    }
}
