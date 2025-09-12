package com.loopers.interfaces.scheduler;

import com.loopers.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingScheduler {
    private final RankingService rankingService;

    @Scheduled(cron = "0 50 23 * * ?")
    public void carryOver() {
        rankingService.carryOver();
    }
}
