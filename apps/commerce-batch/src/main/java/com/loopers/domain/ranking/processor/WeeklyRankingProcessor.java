package com.loopers.domain.ranking.processor;

import com.loopers.domain.productMetrics.dto.ProductTotalMetricsSummary;
import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import com.loopers.domain.ranking.enums.ProductRankingWeight;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
@RequiredArgsConstructor
public class WeeklyRankingProcessor implements ItemProcessor<ProductTotalMetricsSummary, MvProductRankWeekly> {

    @Value("#{jobParameters['startDate']}")
    private LocalDate startDate;

    @Value("#{jobParameters['endDate']}")
    private LocalDate endDate;

    @Value("#{jobParameters['aggregateDate']}")
    private LocalDate aggregateDate;

    @Override
    public MvProductRankWeekly process(ProductTotalMetricsSummary item) {
        Double score = (item.likeCount() * ProductRankingWeight.ORDERED.getWeight()) + (item.likeCount()
            * ProductRankingWeight.LIKED.getWeight()) + (item.viewCount() * ProductRankingWeight.VIEWED.getWeight());

        return MvProductRankWeekly.create(item.productId(), score, startDate, endDate, aggregateDate);
    }
}
