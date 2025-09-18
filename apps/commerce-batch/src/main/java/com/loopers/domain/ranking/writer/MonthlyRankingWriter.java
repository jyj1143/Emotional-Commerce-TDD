package com.loopers.domain.ranking.writer;

import com.loopers.domain.ranking.entity.MvProductRankMonthly;
import com.loopers.domain.ranking.repository.MvProductRankMonthlyRepository;
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
public class MonthlyRankingWriter  implements ItemWriter<MvProductRankMonthly> {

    private final MvProductRankMonthlyRepository mvProductRankMonthlyRepository;

    @Override
    public void write(Chunk<? extends MvProductRankMonthly> chunk) throws Exception {
        if (chunk.isEmpty()) {
            return;
        }

        List<MvProductRankMonthly> items = new ArrayList<>(chunk.getItems());
        mvProductRankMonthlyRepository.saveAll(items);
    }
}
