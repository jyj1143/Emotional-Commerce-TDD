package com.loopers.application.product.dto;

public record ProductResult(
    Long id,
    String name,
    Long price,
    String status,
    Long brandId,
    String brandName,
    Long likeCount,
    Long rank
) {

    public static ProductResult of(
        final ProductInfo productInfo
    ) {
        return new ProductResult(
            productInfo.id(),
            productInfo.name(),
            productInfo.price(),
            productInfo.status(),
            productInfo.brandId(),
            productInfo.brandName(),
            productInfo.likeCount(),
            null
        );
    }
    public static ProductResult of(
        final ProductInfo productInfo,
        final Long rank
    ) {
        return new ProductResult(
            productInfo.id(),
            productInfo.name(),
            productInfo.price(),
            productInfo.status(),
            productInfo.brandId(),
            productInfo.brandName(),
            productInfo.likeCount(),
            rank
        );
    }
}
