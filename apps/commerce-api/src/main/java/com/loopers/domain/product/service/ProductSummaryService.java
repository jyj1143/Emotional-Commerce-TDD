package com.loopers.domain.product.service;

import com.loopers.domain.product.dto.product.ProductCommand;
import com.loopers.domain.product.dto.summary.ProductSummaryInfo;
import com.loopers.domain.product.repository.ProductSummaryRepository;
import com.loopers.support.pagenation.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSummaryService {

    private final ProductSummaryRepository productSummaryRepository;

    public PageResult<ProductSummaryInfo> findAll(ProductCommand.ProductSummary criteria) {
        return productSummaryRepository.findAll(criteria);
    }
}
