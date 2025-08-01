package com.loopers.domain.brand.service;

import com.loopers.domain.brand.entity.BrandModel;
import com.loopers.domain.brand.repository.BrandRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandModel get(Long id) {
        return brandRepository.find(id).orElseThrow(
            () -> new CoreException(ErrorType.NOT_FOUND, "브랜드를 찾을 수 없습니다."));
    }

    @Transactional
    public BrandModel save(BrandModel brand) {
        return brandRepository.save(brand);
    }

    @Transactional
    public List<BrandModel> saveAll(List<BrandModel> brands) {
        return brandRepository.saveAll(brands);
    }

}
