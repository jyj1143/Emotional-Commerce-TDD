package com.loopers.infrastructure.product;

import com.loopers.domain.product.dto.product.ProductCommand.ProductSummary;
import com.loopers.domain.product.repository.ProductSummaryDenormalizedCustomRepository;
import com.loopers.infrastructure.product.dto.ProductRow;
import com.loopers.support.pagenation.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductSummaryDenormalizedRepositoryImpl  implements ProductSummaryDenormalizedCustomRepository {

    private final ProductSummaryDenormalizedQueryDslRepository productSummaryDenormalizedQueryDslRepository;

    @Override
    public PageResult<ProductRow.ProductSummary> findAll(ProductSummary criteria) {
        return productSummaryDenormalizedQueryDslRepository.findAll(criteria);
    }
}
