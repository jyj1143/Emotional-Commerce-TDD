package com.loopers.domain.product.service;

import com.loopers.domain.product.entity.ProductModel;
import com.loopers.domain.product.repository.ProductRepository;
import com.loopers.infrastructure.product.dto.ProductInfo.ProductWithBrand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductModel save(ProductModel product) {
        return productRepository.save(product);
    }

    @Transactional
    public List<ProductModel> saveAll(List<ProductModel> products) {
        return productRepository.saveAll(products);
    }

    public  List<ProductWithBrand> findProductWithBrand(){
        return productRepository.findProductWithBrand();
    }

}
