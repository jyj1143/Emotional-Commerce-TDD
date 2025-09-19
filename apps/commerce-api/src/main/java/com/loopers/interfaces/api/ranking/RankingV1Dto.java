package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.dto.RankingCriteria;
import com.loopers.application.ranking.dto.RankingResult;
import com.loopers.domain.ranking.enums.RankingPeriod;
import com.loopers.support.pagenation.Pageable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class RankingV1Dto {
    public record SearchRankingsRequest(
        @NotNull
        @Positive
        Integer page,
        @NotNull
        @Positive
        Integer size,
        @PastOrPresent
        LocalDate date,
        RankingPeriod rankingPeriod
    ) {
        public  RankingCriteria.SearchRankings toCriteria() {
            return new RankingCriteria.SearchRankings(
                    new Pageable(page, size), date, rankingPeriod
            );
        }
    }

    public record SearchRankingsResponse(
        Long productId,
        String productName,
        Long price,
        String saleStatus,
        Long brandId,
        String brandName,
        Long rank
    ){
        public static SearchRankingsResponse from(RankingResult result) {
            return new SearchRankingsResponse(
                result.productId(),
                result.productName(),
                result.price(),
                result.saleStatus(),
                result.brandId(),
                result.brandName(),
                result.rank()
            );
        }
    }
}
