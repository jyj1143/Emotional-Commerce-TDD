package com.loopers.domain.product.repository;

import com.loopers.domain.product.dto.product.ProductCommand;
import com.loopers.domain.product.dto.summary.ProductSummaryInfo;
import com.loopers.support.pagenation.PageResult;

public interface ProductSummaryRepository {
    PageResult<ProductSummaryInfo> findAll( ProductCommand.ProductSummary criteria);
}
