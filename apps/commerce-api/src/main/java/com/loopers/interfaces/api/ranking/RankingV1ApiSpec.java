package com.loopers.interfaces.api.ranking;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.ranking.RankingV1Dto.SearchRankingsResponse;
import com.loopers.support.pagenation.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Ranking V1 API", description = "Ranking V1 API 입니다.")
public interface RankingV1ApiSpec {

    @Operation(
        summary = "상품 랭킹 조회",
        description = "상품 랭킹을 조회합니다."
    )
    ApiResponse<PageResult<SearchRankingsResponse>> searchRankings(
        @Valid
        RankingV1Dto.SearchRankingsRequest request
    );
}
