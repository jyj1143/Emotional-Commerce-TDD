package com.loopers.domain.product.service;


import com.loopers.domain.product.dto.product.ProductEvent;
import com.loopers.domain.product.dto.product.ProductInfo;
import com.loopers.domain.product.entity.ProductModel;
import com.loopers.domain.product.repository.ProductRepository;
import com.loopers.infrastructure.product.dto.ProductInfo.ProductWithBrand;
import java.util.List;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventPublisher productEventPublisher;

    @Transactional
    public ProductInfo save(ProductModel product) {
        ProductModel productModel = productRepository.save(product);
        productEventPublisher.publish(ProductEvent.Register.from(productModel));
        return ProductInfo.from(productModel);
    }

    @Transactional
    public List<ProductModel> saveAll(List<ProductModel> products) {
        return productRepository.saveAll(products);
    }

    public  List<ProductWithBrand> findProductWithBrand(){
        return productRepository.findProductWithBrand();
    }

    public ProductModel get(Long id) {
        return productRepository.find(id)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당 상품을 찾을 수 없습니다. id: " + id));
    }

}
