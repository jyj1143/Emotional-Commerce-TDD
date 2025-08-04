package com.loopers.domain.product.repository;

import com.loopers.domain.product.entity.ProductModel;
import com.loopers.infrastructure.product.dto.ProductInfo.ProductWithBrand;
import java.util.List;
import java.util.Optional;



public interface ProductRepository {
    List<ProductModel> saveAll(List<ProductModel> products);
    ProductModel save(ProductModel product);
    Optional<ProductModel> find(Long id);
    List<ProductWithBrand> findProductWithBrand();
}
