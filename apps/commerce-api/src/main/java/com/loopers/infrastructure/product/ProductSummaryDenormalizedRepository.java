package com.loopers.infrastructure.product;

import com.loopers.domain.product.entity.ProductSummaryModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSummaryDenormalizedRepository extends JpaRepository<ProductSummaryModel, Long> {

    Optional<ProductSummaryModel> findByRefProductId(Long refProductId);

}
