package com.loopers.infrastructure.brand;

import com.loopers.domain.brand.entity.BrandModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandJpaRepository extends JpaRepository<BrandModel, Long> {
}
