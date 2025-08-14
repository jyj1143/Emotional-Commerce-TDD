package com.loopers.application.product.dto;

import com.loopers.domain.product.dto.product.ProductCommand;
import com.loopers.domain.product.enums.ProductSortType;
import com.loopers.support.pagenation.SortOrder;

public record ProductCriteria (
){

    public record GetProductSummary(
        Integer page,
        Integer size,
        ProductSortType sortType,
        SortOrder sortOrder
    ) {
        public ProductCommand.ProductSummary toProductCommand() {
            return new ProductCommand.ProductSummary(page, size, sortType, sortOrder);
        }
    }

    public record GetProduct(Long productId) {

        public ProductCommand.GetProduct toProductCommand() {
            return new ProductCommand.GetProduct(productId);
        }
    }
}
