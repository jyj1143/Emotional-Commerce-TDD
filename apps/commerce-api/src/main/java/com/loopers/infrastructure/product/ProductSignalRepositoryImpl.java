package com.loopers.infrastructure.product;

import com.loopers.domain.product.entity.ProductSignalModel;
import com.loopers.domain.product.repository.ProductSignalRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductSignalRepositoryImpl implements ProductSignalRepository {

    private final ProductSignalJpaRepository productSignalJpaRepository;

    public ProductSignalModel save(ProductSignalModel productSignalModel) {
        return productSignalJpaRepository.save(productSignalModel);
    }

    public Optional<ProductSignalModel> findByProductId(Long productId) {
        return productSignalJpaRepository.findByRefProductId(productId);
    }

}
