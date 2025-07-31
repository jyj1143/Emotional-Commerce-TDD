package com.loopers.infrastructure.brand;

import com.loopers.domain.brand.entity.BrandModel;
import com.loopers.domain.brand.repository.BrandRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {
    private final BrandJpaRepository brandJpaRepository;

    @Override
    public Optional<BrandModel> find(Long id) {
        return brandJpaRepository.findById(id);
    }

    @Override
    public BrandModel save(BrandModel brand) {
        return brandJpaRepository.save(brand);
    }

    @Override
    public List<BrandModel> saveAll(List<BrandModel> brands) {
        return brandJpaRepository.saveAll(brands);
    }
}
