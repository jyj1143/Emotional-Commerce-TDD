package com.loopers.domain.product.dto.product;

import com.loopers.domain.product.entity.ProductModel;
import com.loopers.domain.product.enums.SaleStatus;
import java.time.LocalDate;

public record ProductEvent() {

    public record Register(
        Long productId,
        String name,
        Long price,
        SaleStatus saleStatus,
        LocalDate saleDate
    ) {
        public static Register from(ProductModel product) {
            return new Register(product.getId()
                , product.getName().getName()
                , product.getSalePrice().getAmount()
                , product.getSaleStatus()
                , product.getSaleDate().getSaleDate()
            );
        }
    }

}
