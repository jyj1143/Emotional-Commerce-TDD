package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.dto.OrderCriteria;
import com.loopers.application.order.dto.OrderCriteria.Order;
import com.loopers.application.order.dto.OrderResult;
import com.loopers.domain.inventory.InventoryModel;
import com.loopers.domain.inventory.repository.InventoryRepository;
import com.loopers.domain.order.enums.OrderStatus;
import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.repository.PointRepository;
import com.loopers.domain.product.entity.ProductSkuModel;
import com.loopers.domain.product.enums.SaleStatus;
import com.loopers.domain.product.repository.ProductSkuRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderFacadeIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private ProductSkuRepository productSkuRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private PointRepository pointRepository;

    @DisplayName("주문을 생성할 때, ")
    @Nested
    class Create {

        @DisplayName("정상적인 주문 생성 요청을 하면 주문 생성후 재고 차감 성공한다.")
        @Test
        void when_validOrderRequestGiven_then_createOrder() {
            Long orderQuantity = 10L;
            Long inventoryQuantity = 100L;

            // given
            ProductSkuModel productSkuModel = ProductSkuModel.of(0L, "색상", "RED"
                , SaleStatus.ON_SALE, 1L);
            ProductSkuModel savedProductSkuModel = productSkuRepository.save(productSkuModel);

            InventoryModel savedInventoryModel = inventoryRepository.save(InventoryModel.of(inventoryQuantity, savedProductSkuModel.getId()));
            PointModel pointModel = PointModel.of(1000L, 1L);
            pointRepository.save(pointModel);
            pointRepository.increase(1L, 50000L);

            Order order = new OrderCriteria.Order(
                1L,
                List.of(OrderItemModel.of(orderQuantity, 10L, savedProductSkuModel.getId())));
            OrderResult saved = orderFacade.order(order);
            // when

            Long currentQuantity= inventoryRepository.find(savedProductSkuModel.getId()).get().getQuantity().getQuantity();
            // then
            assertAll(
                () -> assertThat(saved.id()).isNotNull(),
                () -> assertThat(saved.userId()).isEqualTo(pointModel.getRefUserId()),
                () -> assertThat(currentQuantity).isEqualTo(inventoryQuantity - orderQuantity)
            );
        }
    }
}
