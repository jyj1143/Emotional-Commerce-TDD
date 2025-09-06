package com.loopers.domain.inventory.dto;

public record InventoryGlobalEvent() {

    public record StockChanged(
        Long productSkuId,
        Long quantity
    ) {
        public static StockChanged from(Long productSkuId, Long quantity) {
            return new StockChanged(productSkuId, quantity);
        }
    }
}
