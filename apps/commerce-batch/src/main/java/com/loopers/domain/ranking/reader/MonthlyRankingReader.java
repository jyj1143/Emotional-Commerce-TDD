package com.loopers.domain.ranking.reader;

import com.loopers.domain.productMetrics.dto.ProductTotalMetricsSummary;
import com.loopers.domain.productMetrics.repository.ProductMetricsRepository;
import com.loopers.domain.ranking.entity.MvProductRankMonthly;
import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import com.loopers.domain.ranking.repository.MvProductRankWeeklyRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@StepScope
@Component
@RequiredArgsConstructor
public class MonthlyRankingReader implements ItemReader<ProductTotalMetricsSummary> {

    private final ProductMetricsRepository productMetricsRepository;

    @Value("#{jobParameters['aggregateDate']}")
    private LocalDate aggregateDate;

    private int pageSize = 100;
    private int currentPage = 0;
    private Iterator<ProductTotalMetricsSummary> currentPageItems;
    private boolean isFinished = false;

    @Override
    public ProductTotalMetricsSummary read() {
        if (isFinished) {
            return null;
        }

        if (currentPageItems == null || !currentPageItems.hasNext()) {
            fetchNextPage();
        }

        if (currentPageItems.hasNext()) {
            return currentPageItems.next();
        } else {
            return null;
        }
    }

    private void fetchNextPage() {
        YearMonth yearMonth = YearMonth.from(aggregateDate);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize);
        Page<ProductTotalMetricsSummary> page = productMetricsRepository.findTotalSummary(startDate, endDate, pageRequest);

        if (page.hasContent()) {
            currentPageItems = page.getContent().iterator();
            currentPage++;
        } else {
            isFinished = true;
            currentPageItems = Collections.emptyIterator();
        }
    }
}
