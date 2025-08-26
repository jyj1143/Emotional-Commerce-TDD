package com.loopers.domain.order.dto;

import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.enums.OrderStatus;
import java.util.List;

public record OrderInfo(
    Long id,
    Long userId,
    OrderStatus status,
    Long totalPrice,
    List<OrderItem> orderItems
) {

    public static OrderInfo from(final OrderModel order) {
        return new OrderInfo(
            order.getId(),
            order.getRefUserId(),
            order.getStatus(),
            order.calculateTotalPrice(),
            order.getOrderItemModels().stream().
                map(item ->
                    new OrderItem(
                        item.getId(),
                        item.getOrder().getId(),
                        item.getQuantity().getQuantity(),
                        item.getPurchasePrice().getAmount(),
                        item.getRefProductSkuId()
                    )
                ).toList());
    }


    public record OrderItem(
        Long id,
        Long orderId,
        Long quantity,
        Long purchasePrice,
        Long productSkuId
    ) {

    }

}
