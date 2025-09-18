package com.loopers.domain.ranking.repository;

import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MvProductRankWeeklyRepository {

    void saveAll(List<MvProductRankWeekly> weeklyRankings);

    Page<MvProductRankWeekly> findByBetweenDate(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
