package com.loopers.interfaces.consumer.event;

public record ProductEvent() {
    public record StockChanged(
        Long productSkuId,
        Integer quantity
    ) {
    }
}
