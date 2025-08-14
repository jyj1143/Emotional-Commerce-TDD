package com.loopers.domain.product.cache;

import com.loopers.application.product.dto.ProductInfo;
import com.loopers.domain.product.dto.product.ProductCommand;
import com.loopers.domain.product.dto.summary.ProductSummaryInfo;
import com.loopers.support.pagenation.PageResult;

import java.util.Optional;


public interface ProductCacheRepository {
    Optional<ProductInfo> findProductDetail(Long productId);
    void saveProductDetail(Long productId, ProductInfo productInfo);

    Optional<PageResult<ProductSummaryInfo>> findProductSummary(ProductCommand.ProductSummary criteria);
    void saveProductSummary(ProductCommand.ProductSummary criteria, PageResult<ProductSummaryInfo> summaryInfo);

    String generateProductDetailKey(Long productId);
    String generateSummaryKey(ProductCommand.ProductSummary criteria);
}
