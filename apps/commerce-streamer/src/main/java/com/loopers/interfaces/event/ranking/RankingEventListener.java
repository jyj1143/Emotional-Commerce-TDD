package com.loopers.interfaces.event.ranking;

import com.loopers.domain.productMetrics.dto.ProductMetricsEvent;
import com.loopers.domain.ranking.dto.RankingCommand;
import com.loopers.domain.ranking.dto.RankingCommand.Like;
import com.loopers.domain.ranking.dto.RankingCommand.Order;
import com.loopers.domain.ranking.dto.RankingCommand.UnLike;
import com.loopers.domain.ranking.dto.RankingCommand.View;
import com.loopers.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RankingEventListener {

    private final RankingService rankingService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProductMetricsEvent.ViewList event) {
        rankingService.scoreView(event.views().stream()
            .map(like -> new View(like.productId()))
            .toList());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProductMetricsEvent.LikeList event) {
        rankingService.scoreLike(event.likes().stream()
            .map(like -> new Like(like.productId()))
            .toList());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProductMetricsEvent.UnLikeList event) {
        rankingService.scoreUnLike(event.likes().stream()
            .map(like -> new UnLike(like.productId()))
            .toList());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProductMetricsEvent.OrderList event) {
        rankingService.scoreOrder(event.orders().stream()
            .map(like -> new Order(like.productId()))
            .toList());
    }

}
