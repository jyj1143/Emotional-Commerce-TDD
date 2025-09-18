package com.loopers.domain.ranking.reader;

import com.loopers.domain.productMetrics.dto.ProductTotalMetricsSummary;
import com.loopers.domain.productMetrics.repository.ProductMetricsRepository;
import java.time.LocalDate;
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
public class WeeklyRankingReader implements ItemReader<ProductTotalMetricsSummary> {

    private final ProductMetricsRepository productMetricsRepository;

    @Value("#{jobParameters['startDate']}")
    private LocalDate startDate;

    @Value("#{jobParameters['endDate']}")
    private LocalDate endDate;

    private int pageSize = 100;
    private int currentPage = 0;
    private Iterator<ProductTotalMetricsSummary> currentPageItems;
    private boolean isFinished = false;

    @Override
    public ProductTotalMetricsSummary read() {
        if (isFinished) {
            return null;
        }

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate와 endDate는 null이 될 수 없습니다.");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate는 startDate 이후여야 합니다.");
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
