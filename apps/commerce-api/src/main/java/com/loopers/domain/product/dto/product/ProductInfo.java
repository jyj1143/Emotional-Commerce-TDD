package com.loopers.domain.product.dto.product;

import com.loopers.domain.product.entity.ProductModel;
import com.loopers.domain.product.enums.SaleStatus;

public record ProductInfo (
    Long id,
    String name,
    Long price,
    SaleStatus saleStatus
){
    public static ProductInfo from(ProductModel product) {
        return new ProductInfo(
            product.getId(),
            product.getName().getName(),
            product.getSalePrice().getAmount(),
            product.getSaleStatus()
        );
    }
}
