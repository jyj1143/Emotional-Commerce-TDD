package com.loopers.domain.ranking.repository;

import com.loopers.support.pagenation.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface RankingInMemoryRepository {

    List<Long> getProductRanking(Pageable pageable, LocalDate date);

    Long countRankedProducts(LocalDate date);

    Long getRankOfProduct(Long productId, LocalDate date);

}
