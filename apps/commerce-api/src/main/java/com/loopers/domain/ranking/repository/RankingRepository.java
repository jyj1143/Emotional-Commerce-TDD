package com.loopers.domain.ranking.repository;

import com.loopers.domain.ranking.entity.MvProductRankMonthly;
import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import com.loopers.support.pagenation.PageResult;
import com.loopers.support.pagenation.Pageable;


public interface RankingRepository {
    PageResult<MvProductRankWeekly> findWeeklyRankingByYearWeek(String yearWeek, Pageable pageable);

    PageResult<MvProductRankMonthly> findMonthlyRankingByYearMonth(String yearMonth, Pageable pageable);
}
