package com.loopers.domain.ranking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "mv_product_rank_monthly",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "year_month"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MvProductRankMonthly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "aggregat_date", nullable = false)
    private LocalDate aggregateDate;

    @Column(name = "year_month", nullable = false)
    private String yearMonth;

    private MvProductRankMonthly(Long productId, Double score, LocalDate aggregateDate) {
        this.productId = productId;
        this.score = score;
        this.aggregateDate = aggregateDate;
        YearMonth ym = YearMonth.from(aggregateDate);
        this.yearMonth = ym.format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    public static MvProductRankMonthly create(Long productId, Double score, LocalDate aggregateDate) {
        return new MvProductRankMonthly(productId, score, aggregateDate);
    }
}
