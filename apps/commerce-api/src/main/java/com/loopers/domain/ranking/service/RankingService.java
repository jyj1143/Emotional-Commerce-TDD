package com.loopers.domain.ranking.service;

import com.loopers.domain.ranking.dto.RankingCommand;
import com.loopers.domain.ranking.dto.RankingInfo;
import com.loopers.domain.ranking.entity.MvProductRankMonthly;
import com.loopers.domain.ranking.entity.MvProductRankWeekly;
import com.loopers.domain.ranking.repository.RankingInMemoryRepository;
import com.loopers.domain.ranking.repository.RankingRepository;
import com.loopers.support.pagenation.PageResult;
import com.loopers.support.pagenation.PageResult.PaginationInfo;
import com.loopers.support.pagenation.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final RankingInMemoryRepository rankingInMemoryRepository;

    public PageResult<RankingInfo> getProductRanking(RankingCommand.Search command) {
        Pageable pageable = command.pageable();
        LocalDate date = command.date();


        Long totalCount = rankingInMemoryRepository.countRankedProducts(date);

        List<Long> productIds = rankingInMemoryRepository.getProductRanking(pageable, date);

        // 순위 정보 생성
        List<RankingInfo> rankingInfos = IntStream.range(0, productIds.size())
            .mapToObj(i -> new RankingInfo(
                productIds.get(i),
                (long) (pageable.getOffset() + i + 1)
            ))
            .collect(Collectors.toList());

        // 페이지네이션 정보 계산
        int currentPage = pageable.getPage();
        int pageSize = pageable.getSize();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        boolean hasNext = currentPage < totalPages;
        boolean hasPrevious = currentPage > 1;


        // 페이지네이션 정보 생성
        PaginationInfo pagination = new PaginationInfo(
            currentPage,
            totalPages,
            pageSize,
            totalCount,
            hasNext,
            hasPrevious
        );

        return new PageResult<>(rankingInfos, pagination);
    }


    public RankingInfo getProductRanking(RankingCommand.GetRank command) {
        Long rank = rankingInMemoryRepository.getRankOfProduct(command.productId(), command.date());
        return new RankingInfo(command.productId(), rank);
    }

    public PageResult<RankingInfo> getProductWeeklyRanking(RankingCommand.SearchWeekly command) {
        PageResult<MvProductRankWeekly> weeklyRankingByYearWeek = rankingRepository.findWeeklyRankingByYearWeek(command.yearWeek(), command.pageable());
        return weeklyRankingByYearWeek.map(mv -> {
            int index = weeklyRankingByYearWeek.content().indexOf(mv);
            long rank = command.pageable().getOffset() + index + 1;
            return new RankingInfo(mv.getProductId(), rank);
        });
    }

    public PageResult<RankingInfo> getProductMonthlyRanking(RankingCommand.SearchMonthly command) {
        PageResult<MvProductRankMonthly> monthlyRankingByYearMonth = rankingRepository.findMonthlyRankingByYearMonth(command.yearMonth(), command.pageable());
        return monthlyRankingByYearMonth.map(mv -> {
            int index = monthlyRankingByYearMonth.content().indexOf(mv);
            long rank = command.pageable().getOffset() + index + 1;
            return new RankingInfo(mv.getProductId(), rank);
        });
    }

}
