package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.entity.MvProductRankMonthly;
import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import com.loopers.domain.ranking.repository.RankingRepository;
import com.loopers.support.pagenation.PageResult;
import com.loopers.support.pagenation.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {

    private final WeeklyRankingJpaRepository weeklyRankingJpaRepository;
    private final MonthlyRankingJpaRepository monthlyRankingJpaRepository;


    @Override
    public PageResult<MvProductRankWeekly> findWeeklyRankingByYearWeek(String yearWeek, Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPage(), pageable.getSize());
        Page<MvProductRankWeekly> weeklyRankingByYearWeek = weeklyRankingJpaRepository.findWeeklyRankingByYearWeek(yearWeek, pageRequest);
        return PageResult.of(weeklyRankingByYearWeek);
    }

    @Override
    public PageResult<MvProductRankMonthly> findMonthlyRankingByYearMonth(String yearMonth, Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPage(), pageable.getSize());
        Page<MvProductRankMonthly> monthlyRankingByYearMonth = monthlyRankingJpaRepository.findMonthlyRankingByYearMonth(yearMonth, pageRequest);
        return PageResult.of(monthlyRankingByYearMonth);
    }
}
