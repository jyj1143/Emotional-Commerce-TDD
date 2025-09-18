package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import com.loopers.domain.ranking.repository.MvProductRankWeeklyRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MvProductRankWeeklyRepositoryImpl implements MvProductRankWeeklyRepository {

    private final MvProductRankWeeklyJpaRepository mvProductRankWeeklyJpaRepository;

    @Override
    public void saveAll(List<MvProductRankWeekly> weeklyRankings) {
        for (MvProductRankWeekly ranking : weeklyRankings) {
            mvProductRankWeeklyJpaRepository.merge(
                ranking.getProductId(),
                ranking.getScore(),
                ranking.getStartDate(),
                ranking.getEndDate(),
                ranking.getAggregateDate(),
                ranking.getYearWeek()
            );
        }
    }

    @Override
    public Page<MvProductRankWeekly> findByBetweenDate(LocalDate startDate,LocalDate endDate, Pageable pageable) {
        return mvProductRankWeeklyJpaRepository.findByBetweenDate(startDate, endDate, pageable);
    }
}
