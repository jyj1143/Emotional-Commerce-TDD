package com.loopers.infrastructure.product;

import com.loopers.domain.product.dto.product.ProductCommand;
import com.loopers.domain.product.dto.summary.ProductSummaryInfo;
import com.loopers.domain.product.repository.ProductSummaryRepository;
import com.loopers.support.pagenation.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductSummaryRepositoryImpl implements ProductSummaryRepository {
    private final ProductQueryDslRepositoryImpl productQueryDslRepository;

    @Override
    public PageResult<ProductSummaryInfo> findAll(ProductCommand.ProductSummary criteria) {
        return productQueryDslRepository.findProductSummary(criteria);
    }

}
