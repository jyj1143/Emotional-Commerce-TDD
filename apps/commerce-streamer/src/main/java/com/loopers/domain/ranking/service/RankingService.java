package com.loopers.domain.ranking.service;

import com.loopers.domain.ranking.dto.RankingCommand;
import com.loopers.domain.ranking.repository.RankingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;


    public void scoreView(List<RankingCommand.View> commands) {
        for (RankingCommand.View command : commands) {
            rankingRepository.scoreView(command.productId());
        }
    }

    public void scoreLike(List<RankingCommand.Like> commands) {
        for (RankingCommand.Like command : commands) {
            rankingRepository.scoreView(command.productId());
        }
    }

    public void scoreUnLike(List<RankingCommand.UnLike> commands) {
        for (RankingCommand.UnLike command : commands) {
            rankingRepository.scoreView(command.productId());
        }
    }

    public void scoreOrder(List<RankingCommand.Order> commands) {
        for (RankingCommand.Order command : commands) {
            rankingRepository.scoreView(command.productId());
        }
    }

    public void carryOver() {
        rankingRepository.carryOver();
    }
}
