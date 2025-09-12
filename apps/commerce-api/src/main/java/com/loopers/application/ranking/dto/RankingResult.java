package com.loopers.application.ranking.dto;


public record RankingResult(
    Long productId,
    String productName,
    Long price,
    String saleStatus,
    Long brandId,
    String brandName,
    Long rank
) {
}
