package com.loopers.interfaces.api.product;

import com.loopers.application.product.dto.ProductCriteria;
import com.loopers.application.product.dto.ProductResult;
import com.loopers.application.product.dto.ProductSummaryResult;
import com.loopers.domain.product.dto.summary.ProductSummaryInfo;
import com.loopers.domain.product.enums.ProductSortType;
import com.loopers.support.pagenation.PageResult;
import com.loopers.support.pagenation.PageResult.PaginationInfo;
import com.loopers.support.pagenation.SortOrder;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class ProductV1Dto {

    public record GetProductSummaryRequest(
        @NotNull
        Integer page,
        @NotNull
        Integer size,
        @NotNull
        ProductSortType sortType,
        @NotNull
        SortOrder sortOrder
    ) {

        public ProductCriteria.GetProductSummary toProductCriteria() {
            return new ProductCriteria.GetProductSummary(
                page,
                size,
                sortType,
                sortOrder
            );
        }
    }

    public record GetProductSummaryResponse(
        Long id,
        String name,
        Long salePrice,
        LocalDate saleDate,
        Long brandId,
        String brandName,
        Long likeCount
    ) {

        public static GetProductSummaryResponse from(
            ProductSummaryResult productSummary
        ) {
            return new GetProductSummaryResponse(
                productSummary.id(),
                productSummary.name(),
                productSummary.salePrice(),
                productSummary.saleDate(),
                productSummary.brandId(),
                productSummary.brandName(),
                productSummary.likeCount()
            );
        }
    }


    public record SearchProductSummaryResponse(
        List<GetProductSummaryResponse> content,
        PaginationInfo paginationInfo
    ) {

        public static SearchProductSummaryResponse from(
            PageResult<ProductSummaryResult> productSummary
        ) {
            return new SearchProductSummaryResponse(
                productSummary.content().stream()
                    .map(GetProductSummaryResponse::from)
                    .toList(),
                productSummary.paginationInfo()
            );
        }
    }

    public record GetResponse(
        Long id,
        String name,
        Long price,
        String status,
        Long brandId,
        String brandName,
        Long likeCount
    ) {
        public static GetResponse from(ProductResult result) {
            return new GetResponse(
                result.id(),
                result.name(),
                result.price(),
                result.status(),
                result.brandId(),
                result.brandName(),
                result.likeCount()
            );
        }
    }

}
