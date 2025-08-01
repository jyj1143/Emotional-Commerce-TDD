package com.loopers.domain.product.dto.summary;


import java.time.LocalDate;

public record ProductSummaryInfo(
    Long id,
    String name,
    Long salePrice,
    LocalDate saleDate,
    Long brandIqd,
    String brandName,
    Long likeCount
) {

}
