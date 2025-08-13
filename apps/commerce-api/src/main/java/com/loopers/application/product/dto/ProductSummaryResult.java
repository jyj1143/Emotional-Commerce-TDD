package com.loopers.application.product.dto;

import com.loopers.domain.product.dto.summary.ProductSummaryInfo;
import java.time.LocalDate;

public record ProductSummaryResult (
    Long id,
    String name,
    Long salePrice,
    LocalDate saleDate,
    Long brandId,
    String brandName,
    Long likeCount
){


    public static ProductSummaryResult from(
        ProductSummaryInfo productSummary
    ){
        return new ProductSummaryResult(
            productSummary.id(),
            productSummary.name(),
            productSummary.salePrice(),
            productSummary.saleDate(),
            productSummary.brandId(),
            productSummary.brandName(),
            productSummary.likeCount()
        );
    }

}
