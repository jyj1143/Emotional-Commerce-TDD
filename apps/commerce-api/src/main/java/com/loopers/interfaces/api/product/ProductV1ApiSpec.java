package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.ProductV1Dto.GetProductSummaryRequest;
import com.loopers.interfaces.api.product.ProductV1Dto.SearchProductSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Product V1 API", description = "Product V1 API 입니다.")
public interface ProductV1ApiSpec {
    @Operation(
        summary = "상품 목록 검색 및 조회"
    )
    ApiResponse<SearchProductSummaryResponse> searchProducts(
        GetProductSummaryRequest request
    );
}
