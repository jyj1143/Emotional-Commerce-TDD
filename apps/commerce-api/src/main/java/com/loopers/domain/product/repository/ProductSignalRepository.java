package com.loopers.domain.product.repository;

import com.loopers.domain.product.entity.ProductSignalModel;
import java.util.Optional;

public interface ProductSignalRepository {

    ProductSignalModel save(ProductSignalModel productSignalModel);

    Optional<ProductSignalModel> findByProductId(Long productId);
}
