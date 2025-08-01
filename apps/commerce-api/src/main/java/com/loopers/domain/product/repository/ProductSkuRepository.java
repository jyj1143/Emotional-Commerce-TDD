package com.loopers.domain.product.repository;

import com.loopers.domain.product.entity.ProductSkuModel;
import java.util.List;
import java.util.Optional;

public interface ProductSkuRepository {

    List<ProductSkuModel> saveAll(List<ProductSkuModel> skus);

    ProductSkuModel save(ProductSkuModel sku);

    Optional<ProductSkuModel> find(Long id);

    List<ProductSkuModel> findAllById(List<Long> ids);
}
