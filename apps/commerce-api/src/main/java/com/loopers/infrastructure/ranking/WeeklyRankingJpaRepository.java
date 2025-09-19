package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WeeklyRankingJpaRepository extends JpaRepository<MvProductRankWeekly, Long> {

    @Query("""
        SELECT wprm
        FROM MvProductRankWeekly wprm
        WHERE wprm.yearWeek = :yearWeek
        ORDER BY wprm.score ASC
    """)
    Page<MvProductRankWeekly> findWeeklyRankingByYearWeek(
            @Param("yearWeek") String yearWeek,
            Pageable pageable
    );

}
