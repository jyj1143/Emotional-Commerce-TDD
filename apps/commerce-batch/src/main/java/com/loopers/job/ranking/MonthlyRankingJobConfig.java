package com.loopers.job.ranking;

import com.loopers.domain.productMetrics.dto.ProductTotalMetricsSummary;
import com.loopers.domain.ranking.entity.MvProductRankMonthly;
import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import com.loopers.domain.ranking.processor.MonthlyRankingProcessor;
import com.loopers.domain.ranking.reader.MonthlyRankingReader;
import com.loopers.domain.ranking.writer.MonthlyRankingWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MonthlyRankingJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final MonthlyRankingReader monthlyRankingReader;
    private final MonthlyRankingProcessor monthlyRankingProcessor;
    private final MonthlyRankingWriter monthlyRankingWriter;

    @Bean
    public Job monthlyRankingJob() {
        return new JobBuilder("monthlyRankingJob", jobRepository)
            .start(monthlyRankingStep())
            .build();
    }

    @JobScope
    @Bean
    public Step monthlyRankingStep() {
        return new StepBuilder("monthlyRankingStep", jobRepository)
            .<ProductTotalMetricsSummary, MvProductRankMonthly>chunk(1000, transactionManager)
            .reader(monthlyRankingReader)
            .processor(monthlyRankingProcessor)
            .writer(monthlyRankingWriter)
            .build();
    }
}
