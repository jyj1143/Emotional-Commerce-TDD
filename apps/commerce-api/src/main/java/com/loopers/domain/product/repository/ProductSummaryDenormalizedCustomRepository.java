package com.loopers.domain.product.repository;

import com.loopers.domain.product.dto.product.ProductCommand;
import com.loopers.infrastructure.product.dto.ProductRow;
import com.loopers.support.pagenation.PageResult;

public interface ProductSummaryDenormalizedCustomRepository {

    PageResult<ProductRow.ProductSummary> findAll(ProductCommand.ProductSummary criteria);
}
