package com.loopers.domain.ranking.repository;


public interface RankingRepository {

    void scoreView(Long productId);

    void scoreLike(Long productId);

    void scoreUnLike(Long productId);

    void scoreOrder(Long productId);

    void carryOver();
}
