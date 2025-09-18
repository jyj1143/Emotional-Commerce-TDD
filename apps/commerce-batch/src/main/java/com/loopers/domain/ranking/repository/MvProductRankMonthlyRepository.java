package com.loopers.domain.ranking.repository;


import com.loopers.domain.ranking.entity.MvProductRankMonthly;
import java.time.LocalDate;
import java.util.List;

public interface MvProductRankMonthlyRepository {

    void saveAll(List<MvProductRankMonthly> weeklyRankings);

    List<MvProductRankMonthly> findByBetweenDate(LocalDate startDate, LocalDate endDate);

    List<MvProductRankMonthly> findByBetweenDate(String yearMonth);
}
