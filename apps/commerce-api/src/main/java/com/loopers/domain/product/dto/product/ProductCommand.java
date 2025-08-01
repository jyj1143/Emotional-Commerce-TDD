package com.loopers.domain.product.dto.product;

import com.loopers.domain.product.enums.ProductSortType;
import com.loopers.support.pagenation.SortOrder;

public class ProductCommand {

    public record ProductSummary(
        Integer page,
        Integer size,
        ProductSortType sortType,
        SortOrder sortOrder
    ) {

    }

}
