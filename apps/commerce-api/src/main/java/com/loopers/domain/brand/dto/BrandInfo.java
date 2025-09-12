package com.loopers.domain.brand.dto;

import com.loopers.domain.brand.entity.BrandModel;

public record BrandInfo(
    Long id,
    String name
) {
    public static BrandInfo from(BrandModel brand) {
        return new BrandInfo(
            brand.getId(),
            brand.getName().getName()
        );
    }
}
