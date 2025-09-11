package com.loopers.domain.ranking.dto;


public record RankingCommand (){

    public record Like(
        Long productId
    ) {
    }

    public record UnLike(
        Long productId
    ) {
    }

    public record View(
        Long productId
    ) {
    }

    public record Order(
        Long productId
    ) {
    }

}
