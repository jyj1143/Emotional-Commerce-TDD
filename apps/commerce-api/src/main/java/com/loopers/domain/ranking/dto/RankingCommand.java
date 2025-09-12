package com.loopers.domain.ranking.dto;

import com.loopers.support.pagenation.Pageable;
import java.time.LocalDate;

public class RankingCommand {
    public record Search(
        Pageable pageable,
        LocalDate date
    ) {
    }
}
