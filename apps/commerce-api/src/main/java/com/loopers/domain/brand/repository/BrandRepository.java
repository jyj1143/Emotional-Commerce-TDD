package com.loopers.domain.brand.repository;

import com.loopers.domain.brand.entity.BrandModel;
import java.util.List;
import java.util.Optional;

public interface BrandRepository {
    Optional<BrandModel> find(Long id);
    BrandModel save(BrandModel brand);
    List<BrandModel> saveAll(List<BrandModel> brands);
}
