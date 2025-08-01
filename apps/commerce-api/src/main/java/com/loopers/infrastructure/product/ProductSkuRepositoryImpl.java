package com.loopers.infrastructure.product;

import com.loopers.domain.product.entity.ProductSkuModel;
import com.loopers.domain.product.repository.ProductSkuRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ProductSkuRepositoryImpl implements ProductSkuRepository {

    private final ProductSkuJpaRepository productSkuJpaRepository;

    @Override
    public List<ProductSkuModel> saveAll(List<ProductSkuModel> skus) {
        return productSkuJpaRepository.saveAll(skus);
    }

    @Transactional
    @Override
    public ProductSkuModel save(ProductSkuModel sku) {
        return productSkuJpaRepository.save(sku);
    }

    @Override
    public Optional<ProductSkuModel> find(Long id) {
        return productSkuJpaRepository.findById(id);
    }

    @Override
    public List<ProductSkuModel> findAllById(List<Long> ids) {
        return productSkuJpaRepository.findAllById(ids);
    }
}
