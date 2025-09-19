package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.entity.MvProductRankMonthly;
import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import com.loopers.domain.ranking.repository.MvProductRankMonthlyRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MvProductRankMonthlyRepositoryImpl implements MvProductRankMonthlyRepository {

    private final MvProductRankMonthlyJpaRepository mvProductRankMonthlyJpaRepository;

    public void saveAll(List<MvProductRankMonthly> weeklyRankings) {
        for (MvProductRankMonthly ranking : weeklyRankings) {
            mvProductRankMonthlyJpaRepository.merge(
                ranking.getProductId(),
                ranking.getScore(),
                ranking.getAggregateDate(),
                ranking.getYearMonth()
            );
        }
    }

    public List<MvProductRankMonthly> findByBetweenDate(LocalDate startDate,LocalDate endDate) {
        return mvProductRankMonthlyJpaRepository.findByBetweenDate(startDate, endDate);
    }

    public List<MvProductRankMonthly> findByBetweenDate(String yearMonth) {
        return mvProductRankMonthlyJpaRepository.findByBetweenDate(yearMonth);
    }
}
