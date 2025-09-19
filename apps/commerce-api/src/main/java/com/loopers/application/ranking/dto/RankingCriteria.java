package com.loopers.application.ranking.dto;

import com.loopers.domain.ranking.enums.RankingPeriod;
import com.loopers.domain.ranking.dto.RankingCommand;
import com.loopers.support.pagenation.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;

public record RankingCriteria() {
    public record SearchRankings(
            Pageable pageable,
            LocalDate date,
            RankingPeriod rankingPeriod
    ) {
        public RankingCommand.Search toDailCommand() {
            return new RankingCommand.Search(pageable, date);
        }

        public RankingCommand.SearchWeekly toWeeklyCommand() {
            WeekFields weekFields = WeekFields.ISO;
            String format = String.format("%d%02d",
                    date.get(weekFields.weekBasedYear()),
                    date.get(weekFields.weekOfWeekBasedYear()));
            return new RankingCommand.SearchWeekly(pageable, format);
        }

        public RankingCommand.SearchMonthly toMonthlyCommand() {
            YearMonth ym = YearMonth.from(date);
            String yyyyMM = ym.format(DateTimeFormatter.ofPattern("yyyyMM"));
            return new RankingCommand.SearchMonthly(pageable, yyyyMM);
        }
    }
}
