package com.loopers.infrastructure.ranking;

import com.loopers.domain.productMetrics.dto.ProductDailyMetricsSummary;
import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MvProductRankWeeklyJpaRepository extends JpaRepository<MvProductRankWeekly, Long> {

    @Modifying
    @Query("""
                insert into MvProductRankWeekly  (productId, score, startDate, endDate, aggregateDate, yearWeek)
                values (:productId, :score, :startDate, :endDate, :aggregateDate, :yearWeek)
                on conflict (productId, yearWeek) do update set
                             score = score + :score,
                             startDate = :startDate,
                             endDate = :endDate,
                             aggregateDate = :aggregateDate
            """)
    int merge(
        @Param("productId") Long productId,
        @Param("score") Double score,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("aggregateDate") LocalDate aggregateDate,
        @Param("yearWeek") String yearWeek
    );

    @Query("""
            SELECT mprw
            FROM MvProductRankWeekly mprw
            WHERE mprw.aggregateDate  <= :startDate
              AND mprw.aggregateDate   >= :endDate
        """)
    Page<MvProductRankWeekly> findByBetweenDate(@Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate, Pageable pageable);

}
