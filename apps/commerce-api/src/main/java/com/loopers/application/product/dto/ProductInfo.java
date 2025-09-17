package com.loopers.application.product.dto;

import com.loopers.domain.brand.dto.BrandInfo;
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
    public static ProductInfo of(com.loopers.domain.product.dto.product.ProductInfo product, BrandInfo brand, Long likeCount) {
        return new ProductInfo(
                product.id(),
                product.name(),
                product.price(),
                product.saleStatus().name(),
                brand.id(),
                brand.name(),
                likeCount
        );
    }
}
