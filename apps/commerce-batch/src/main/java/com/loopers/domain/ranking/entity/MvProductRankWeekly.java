package com.loopers.domain.ranking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "mv_product_rank_weekly",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "year_week"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MvProductRankWeekly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "aggregat_date", nullable = false)
    private LocalDate aggregateDate;

    @Column(name = "year_week", nullable = false)
    private String yearWeek;

    private MvProductRankWeekly(Long productId, Double score, LocalDate startDate, LocalDate endDate, LocalDate aggregateDate) {
        this.productId = productId;
        this.score = score;
        this.startDate = startDate;
        this.endDate = endDate;
        this.aggregateDate = aggregateDate;

        WeekFields weekFields = WeekFields.ISO;
        this.yearWeek = String.format("%d%02d",
            endDate.get(weekFields.weekBasedYear()),
            endDate.get(weekFields.weekOfWeekBasedYear())); // "202538"
    }

    public static MvProductRankWeekly create(Long productId, Double score, LocalDate startDate, LocalDate endDate,
        LocalDate aggregateDate) {
        return new MvProductRankWeekly(productId, score, startDate, endDate, aggregateDate);
    }
}
