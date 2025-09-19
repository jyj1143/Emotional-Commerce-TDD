package com.loopers.domain.ranking.dto;

import com.loopers.support.pagenation.Pageable;
import java.time.LocalDate;

public class RankingCommand {
    public record Search(
        Pageable pageable,
        LocalDate date
    ) {
    }

    public record SearchWeekly(
            Pageable pageable,
            String yearWeek
    ) {
    }

    public record SearchMonthly(
            Pageable pageable,
            String yearMonth
    ) {
    }

    public record GetRank(
        Long productId,
        LocalDate date
    ) {
    }
}
