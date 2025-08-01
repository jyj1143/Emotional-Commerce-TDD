package com.loopers.domain.inventory.dto;

public record InventoryCommand() {

    public record Create(
        Long productSkuId,
        Long quantity
    ) {

    }

    public record IncreaseStock(
        Long productSkuId,
        Long quantity
    ) {

    }

    public record DecreaseStock(
        Long productSkuId,
        Long quantity
    ) {

    }

    public record GetInventory(
        Long productSkuId
    ) {

    }

}
