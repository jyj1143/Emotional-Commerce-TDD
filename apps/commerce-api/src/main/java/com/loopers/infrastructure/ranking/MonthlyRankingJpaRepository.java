package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.entity.MvProductRankMonthly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MonthlyRankingJpaRepository extends JpaRepository<MvProductRankMonthly, Long> {

    @Query("""
        SELECT mprm
        FROM MvProductRankMonthly mprm
        WHERE mprm.yearMonth = :yearMonth
        ORDER BY mprm.score ASC
    """)
    Page<MvProductRankMonthly> findMonthlyRankingByYearMonth(
            @Param("yearMonth") String yearMonth,
            Pageable pageable
    );

}
