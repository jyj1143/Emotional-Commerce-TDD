package com.loopers.infrastructure.product;

import com.loopers.domain.product.entity.ProductModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductModel, Long> {

    Page<ProductModel> findAll(Pageable pageable);
}
