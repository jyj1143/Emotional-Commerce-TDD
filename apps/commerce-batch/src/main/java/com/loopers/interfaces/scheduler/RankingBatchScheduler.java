package com.loopers.interfaces.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job weeklyRankingJob;
    private final Job monthlyRankingJob;

    /**
     * 매주 월요일 새벽 1시 실행
     */
    @Scheduled(cron = "0 0 1 * * MON", zone = "Asia/Seoul")
    public void runWeeklyRankingJob() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate lastWeekStart = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate lastWeekEnd = today.minusWeeks(1).with(DayOfWeek.SUNDAY);
        LocalDate aggregateDate = today;

        log.info("주간 랭킹 배치 작업 시작: 집계기간 {} ~ {}, 집계일 {}", lastWeekStart, lastWeekEnd, aggregateDate);

        try {
            JobParameters parameters = new JobParametersBuilder()
                .addDate("startDate", java.util.Date.from(lastWeekStart.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .addDate("endDate", java.util.Date.from(lastWeekEnd.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .addDate("aggregateDate", java.util.Date.from(aggregateDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .addDate("time", new Date())
                .toJobParameters();

            jobLauncher.run(weeklyRankingJob, parameters);
            log.info("주간 랭킹 배치 작업 요청 성공");
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error("주간 랭킹 배치 작업 실행 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * 매달 1일 새벽 1시 실행
     */
    @Scheduled(cron = "0 0 1 1 * *", zone = "Asia/Seoul")
    public void runMonthlyRankingJob() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate aggregateDate = today;

        log.info("월간 랭킹 배치 작업 시작: 집계일 {}", aggregateDate);

        try {
            JobParameters parameters = new JobParametersBuilder()
                .addDate("aggregateDate", java.util.Date.from(aggregateDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .addDate("time", new Date())
                .toJobParameters();

            jobLauncher.run(monthlyRankingJob, parameters);
            log.info("월간 랭킹 배치 작업 요청 성공");
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error("월간 랭킹 배치 작업 실행 실패: {}", e.getMessage(), e);
        }
    }

}
