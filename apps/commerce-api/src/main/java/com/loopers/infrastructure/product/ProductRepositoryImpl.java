package com.loopers.infrastructure.product;

import com.loopers.domain.product.entity.ProductModel;
import com.loopers.domain.product.repository.ProductRepository;
import com.loopers.infrastructure.product.dto.ProductInfo.ProductWithBrand;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductQueryDslRepositoryImpl productQueryDslRepository;
    private final ProductJpaRepository productJpaRepository;


    @Override
    public List<ProductModel> saveAll(List<ProductModel> products) {
        return productJpaRepository.saveAll(products);
    }

    @Override
    public ProductModel save(ProductModel product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<ProductModel> find(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public List<ProductWithBrand>findProductWithBrand() {
        return productQueryDslRepository.findProductWithBrand();
    }
}
