package com.loopers.interfaces.event;

public record ProductEvent() {
    public record StockChanged(
        Long productSkuId,
        Integer quantity
    ) {
    }
}
