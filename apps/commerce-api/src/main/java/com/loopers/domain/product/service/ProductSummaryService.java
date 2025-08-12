package com.loopers.domain.product.service;

import com.loopers.domain.product.dto.product.ProductCommand;
import com.loopers.domain.product.dto.summary.ProductSummaryInfo;
import com.loopers.domain.product.repository.ProductSummaryDenormalizedCustomRepository;
import com.loopers.domain.product.repository.ProductSummaryRepository;
import com.loopers.infrastructure.product.dto.ProductRow.ProductSummary;
import com.loopers.support.pagenation.PageResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSummaryService {

    private final ProductSummaryRepository productSummaryRepository;
    private final ProductSummaryDenormalizedCustomRepository productSummaryDenormalizedRepository;

    public PageResult<ProductSummaryInfo> findAll(ProductCommand.ProductSummary criteria) {
        return productSummaryRepository.findAll(criteria);
    }

    public PageResult<ProductSummaryInfo> findAllFromDenormalized(ProductCommand.ProductSummary criteria) {
        PageResult<ProductSummary> productSummaryPageResult = productSummaryDenormalizedRepository.findAll(criteria);
        List<ProductSummaryInfo> content = productSummaryPageResult.content().stream().map(ProductSummaryInfo::from).toList();
        return new PageResult<>(
            content,
            productSummaryPageResult.paginationInfo()
        );
    }

}
