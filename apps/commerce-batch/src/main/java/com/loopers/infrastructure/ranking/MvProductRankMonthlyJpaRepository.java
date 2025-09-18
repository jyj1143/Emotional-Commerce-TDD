package com.loopers.infrastructure.ranking;

import com.loopers.domain.productMetrics.dto.ProductDailyMetricsSummary;
import com.loopers.domain.ranking.entity.MvProductRankMonthly;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MvProductRankMonthlyJpaRepository extends JpaRepository<MvProductRankMonthly, Long> {

    @Modifying
    @Query("""
                insert into MvProductRankMonthly  (productId, score,aggregateDate, yearMonth)
                values (:productId, :score, :startDate, :endDate, :aggregateDate, :yearMonth)
                on conflict (productId, yearMonth) do update set
                             score = score + :score,
                             aggregateDate = :aggregateDate
            """)
    int merge(
        @Param("productId") Long productId,
        @Param("score") Double score,
        @Param("aggregateDate") LocalDate aggregateDate,
        @Param("yearMonth") String yearMonth
    );

    @Query("""
            SELECT mprw
            FROM MvProductRankMonthly mprw
            WHERE mprw.aggregateDate  <= :startDate
              AND mprw.aggregateDate   >= :endDate
        """)
    List<MvProductRankMonthly> findByBetweenDate(@Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT mprw
            FROM MvProductRankMonthly mprw
            WHERE mprw.aggregateDate  = :yearMonth
        """)
    List<MvProductRankMonthly> findByBetweenDate(@Param("yearMonth") String yearMonth);

}
