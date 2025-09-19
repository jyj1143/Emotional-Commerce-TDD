package com.loopers.domain.ranking.processor;

import com.loopers.domain.productMetrics.dto.ProductTotalMetricsSummary;
import com.loopers.domain.ranking.entity.MvProductRankMonthly;
import com.loopers.domain.ranking.enums.ProductRankingWeight;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
@RequiredArgsConstructor
public class MonthlyRankingProcessor implements ItemProcessor<ProductTotalMetricsSummary, MvProductRankMonthly> {

    @Value("#{jobParameters['aggregateDate']}")
    private LocalDate aggregateDate;

    @Override
    public MvProductRankMonthly
    process(ProductTotalMetricsSummary item) throws Exception {
        Double score = (item.likeCount() * ProductRankingWeight.ORDERED.getWeight()) + (item.likeCount()
            * ProductRankingWeight.LIKED.getWeight()) + (item.viewCount() * ProductRankingWeight.VIEWED.getWeight());

        return MvProductRankMonthly.create(item.productId(), score, aggregateDate);
    }

}
