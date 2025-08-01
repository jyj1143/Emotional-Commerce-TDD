package com.loopers.application.product.dto;

import com.loopers.domain.brand.entity.BrandModel;
import com.loopers.domain.product.entity.ProductModel;

public record ProductInfo(
        Long id,
        String name,
        long price,
        String status,
        Long brandId,
        String brandName,
        Long likeCount
)  {
    public static ProductInfo of(ProductModel product, BrandModel brand, Long likeCount) {
        return new ProductInfo(
                product.getId(),
                product.getName().getName(),
                product.getSalePrice().getAmount(),
                product.getSaleStatus().name(),
                brand.getId(),
                brand.getName().getName(),
                likeCount
        );
    }
}
