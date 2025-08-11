package com.loopers.domain.product.dto.sku;

import com.loopers.domain.product.enums.SaleStatus;
import java.util.List;

public record ProductSkuCommand() {

    public record Create(
        Long additionalPrice,
        String optionType,
        String optionValue,
        SaleStatus saleStatus,
        Long refProductId
    ) {

    }

    public record GetProSkus(List<Long> ids) {

    }

    public record GetSku(Long id) {

    }

    public record GetValidSku(
        List<OrderSku> skuList
    ) {
    }

    public record OrderSku(
        Long skuId, Long quantity
    ) {
    }

}
