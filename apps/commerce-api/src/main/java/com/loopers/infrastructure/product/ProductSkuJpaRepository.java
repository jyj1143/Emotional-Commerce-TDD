package com.loopers.infrastructure.product;

import com.loopers.domain.product.entity.ProductSkuModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSkuJpaRepository extends JpaRepository<ProductSkuModel, Long> {

}
