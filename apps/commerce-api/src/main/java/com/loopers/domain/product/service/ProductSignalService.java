package com.loopers.domain.product.service;

import com.loopers.domain.product.entity.ProductSignalModel;
import com.loopers.domain.product.repository.ProductSignalRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductSignalService {

    private final ProductSignalRepository productSignalRepository;

    @Transactional
    public ProductSignalModel register(Long productId) {
        if (productSignalRepository.findByProductId(productId).isPresent()) {
            throw new CoreException(ErrorType.CONFLICT, "이미 상품 신호가 존재합니다.");
        }
        ProductSignalModel productSignalModel = ProductSignalModel.of(productId, 0L);
        return productSignalRepository.save(productSignalModel);
    }

    @Transactional
    public void increaseLikeCount(Long productId) {
        ProductSignalModel productSignalModel = productSignalRepository.findByProductId(productId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
        productSignalModel.increaseLikeCount();
    }

    @Transactional
    public void decreaseLikeCount(Long productId) {
        ProductSignalModel productSignalModel = productSignalRepository.findByProductId(productId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
        productSignalModel.decreaseLikeCount();
    }

}
