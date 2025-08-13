package com.loopers.infrastructure.product.dto;

import com.loopers.domain.product.dto.summary.ProductSummaryInfo;
import java.time.LocalDate;

public record ProductRow(
) {
    public record ProductSummary(
        Long id,
        String name,
        Long salePrice,
        LocalDate saleDate,
        Long brandId,
        String brandName,
        Long likeCount
    ){
        public static ProductSummaryInfo from(
            Long id,
            String name,
            Long salePrice,
            LocalDate saleDate,
            Long brandId,
            String brandName,
            Long likeCount
        ) {
            return new ProductSummaryInfo(
                id,
                name,
                salePrice,
                saleDate,
                brandId,
                brandName,
                likeCount
            );
        }
    }
}
