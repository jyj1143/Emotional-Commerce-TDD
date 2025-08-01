package com.loopers.domain.product.dto.sku;

import com.loopers.domain.product.entity.ProductSkuModel;
import com.loopers.domain.product.enums.SaleStatus;

public record ProductSkuInfo(

    Long id,
    String optionType,
    String optionValue,
    Long additionalPrice,
    Long refProductId,
    SaleStatus saleStatus
) {
    public static ProductSkuInfo from(ProductSkuModel productSku) {
        return new ProductSkuInfo(
            productSku.getId(),
            productSku.getOption().getOptionType(),
            productSku.getOption().getOptionType(),
            productSku.getAdditionalPrice().getAmount(),
            productSku.getRefProductId(),
            productSku.getSaleStatus()
        );
    }
}
