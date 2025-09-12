package com.loopers.application.ranking.dto;

import com.loopers.domain.ranking.dto.RankingCommand;
import com.loopers.support.pagenation.Pageable;
import java.time.LocalDate;

public record RankingCriteria() {
    public record SearchRankings(
       Pageable pageable,
       LocalDate date
    ) {
        public RankingCommand.Search toCommand() {
            return new RankingCommand.Search(pageable, date);
        }
    }
}
