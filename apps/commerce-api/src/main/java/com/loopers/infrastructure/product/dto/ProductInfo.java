package com.loopers.infrastructure.product.dto;


public class ProductInfo {

    public record ProductWithBrand(
        Long id,
        String name,
        Long price,
        Long brandId,
        String brandName
    ) {
    }

}
