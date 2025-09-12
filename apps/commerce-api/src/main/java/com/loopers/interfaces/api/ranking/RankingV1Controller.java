package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.RankingFacade;
import com.loopers.application.ranking.dto.RankingResult;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.ranking.RankingV1Dto.SearchRankingsRequest;
import com.loopers.interfaces.api.ranking.RankingV1Dto.SearchRankingsResponse;
import com.loopers.support.pagenation.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rankings")
public class RankingV1Controller implements RankingV1ApiSpec{
    private final RankingFacade rankingFacade;

    @GetMapping
    @Override
    public ApiResponse<PageResult<SearchRankingsResponse>> searchRankings(SearchRankingsRequest request) {
        PageResult<RankingResult> result = rankingFacade.searchRankings(request.toCriteria());
        return ApiResponse.success(result.map(RankingV1Dto.SearchRankingsResponse::from));
    }
}
