package com.loopers.domain.ranking.writer;

import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import com.loopers.domain.ranking.repository.MvProductRankWeeklyRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@StepScope
@Component
@RequiredArgsConstructor
public class WeeklyRankingWriter implements ItemWriter<MvProductRankWeekly> {

    private final MvProductRankWeeklyRepository mvProductRankWeeklyRepository;

    @Override
    public void write(Chunk<? extends MvProductRankWeekly> chunk) {
        if (chunk.isEmpty()) {
            return;
        }
        List<MvProductRankWeekly> items = new ArrayList<>(chunk.getItems());
        mvProductRankWeeklyRepository.saveAll(items);
    }
}
