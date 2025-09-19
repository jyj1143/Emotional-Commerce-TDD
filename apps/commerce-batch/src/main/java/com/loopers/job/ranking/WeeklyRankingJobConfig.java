package com.loopers.job.ranking;

import com.loopers.domain.productMetrics.dto.ProductTotalMetricsSummary;
import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import com.loopers.domain.ranking.processor.WeeklyRankingProcessor;
import com.loopers.domain.ranking.reader.WeeklyRankingReader;
import com.loopers.domain.ranking.writer.WeeklyRankingWriter;
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
public class WeeklyRankingJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final WeeklyRankingReader weeklyRankingReader;
    private final WeeklyRankingProcessor weeklyRankingProcessor;
    private final WeeklyRankingWriter weeklyRankingWriter;

    @Bean
    public Job weeklyRankingJob() {
        return new JobBuilder("weeklyRankingJob", jobRepository)
            .start(weeklyRankingStep())
            .build();
    }

    @JobScope
    @Bean
    public Step weeklyRankingStep() {
        return new StepBuilder("weeklyRankingStep", jobRepository)
            .<ProductTotalMetricsSummary, MvProductRankWeekly>chunk(1000, transactionManager)
            .reader(weeklyRankingReader)
            .processor(weeklyRankingProcessor)
            .writer(weeklyRankingWriter)
            .build();
    }
}
