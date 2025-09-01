package com.loopers.infrastructure.product;

import com.loopers.domain.product.entity.ProductSignalModel;
import com.loopers.domain.product.entity.ProductSkuModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSignalJpaRepository extends JpaRepository<ProductSignalModel, Long> {

    Optional<ProductSignalModel> findByRefProductId(Long productId);
}
