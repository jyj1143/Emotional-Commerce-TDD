package com.loopers.application.order.dto;

import com.loopers.domain.order.dto.OrderCommand;
import com.loopers.domain.product.dto.sku.ProductSkuCommand;
import com.loopers.domain.product.dto.sku.ProductSkuInfo;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OrderCriteria() {

    public record Order(
        Long userId,
        List<OrderItem> orderItems,
        Long couponId
    ) {

        public OrderCommand.Order toOrderCommand(List<ProductSkuInfo> productSkuInfos) {
            Map<Long, Long> productQuantities = orderItems.stream()
                .collect(Collectors.toMap(OrderItem::skuId, OrderItem::quantity));

            List<OrderCommand.Order.OrderItem> items = productSkuInfos.stream()
                .map(productInfo -> new OrderCommand.Order.OrderItem(
                    productQuantities.get(productInfo.id()),
                    productInfo.price(),
                    productInfo.id()
                ))
                .toList();

            return new OrderCommand.Order(userId, items);
        }


        public ProductSkuCommand.GetValidSku toProductCommand() {
            return new ProductSkuCommand.GetValidSku(
                orderItems.stream().map(OrderItem::toProductOption).toList()
            );
        }

        private List<Long> getProductIds() {
            return orderItems.stream()
                .map(item -> item.skuId)
                .toList();
        }

        public record OrderItem(
            Long skuId,
            Long quantity
        ) {
            public ProductSkuCommand.OrderSku toProductOption() {
                return new ProductSkuCommand.OrderSku(skuId, quantity);
            }
        }
    }


}
