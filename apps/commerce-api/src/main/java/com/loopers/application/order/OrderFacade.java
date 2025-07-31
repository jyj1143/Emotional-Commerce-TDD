package com.loopers.application.order;

import com.loopers.application.order.dto.OrderCriteria;
import com.loopers.application.order.dto.OrderResult;
import com.loopers.domain.inventory.dto.InventoryCommand;
import com.loopers.domain.inventory.service.InventoryService;
import com.loopers.domain.order.OrderItemModel;
import com.loopers.domain.order.dto.OrderCommand.Order;
import com.loopers.domain.order.dto.OrderInfo;
import com.loopers.domain.order.service.OrderService;
import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.service.PaymentService;
import com.loopers.domain.point.service.PointService;
import com.loopers.domain.point.service.dto.PointCommand.UsePoint;
import com.loopers.domain.product.dto.sku.ProductSkuCommand;
import com.loopers.domain.product.service.ProductSkuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final PointService pointService;
    private final ProductSkuService productSkuService;

    @Transactional
    public OrderResult order(OrderCriteria.Order criteria) {

        // 상품 SKU 검증
        productSkuService.getSkus(new ProductSkuCommand.GetProSkus(criteria.items().stream()
            .map(OrderItemModel::getRefProductSkuId)
            .toList()));

        // 주문 생성
        OrderInfo orderInfo = orderService.placeOrder(new Order(
            criteria.userId(),
            criteria.items()
        ));

        // 포인트 사용
        pointService.usePoint(new UsePoint(criteria.userId(), orderInfo.totalPrice()));

        // 재고 감소
        criteria.items().forEach(item ->
            inventoryService.decrease(new InventoryCommand.DecreaseStock(
                item.getRefProductSkuId(),
                item.getQuantity().getQuantity()))
        );

        // 결제 처리
        paymentService.pay(new PaymentCommand.Pay(
            orderInfo.id(),
            PaymentMethod.POINT,
            orderInfo.totalPrice()
        ));

        return OrderResult.from(orderInfo);
    }


}
