package com.loopers.domain.product.entity;

import com.loopers.domain.brand.entity.BrandModel;
import com.loopers.domain.product.enums.SaleStatus;
import java.util.List;

public class ProductFixture {
    // 단일 상품
    public static ProductModel createProduct(Long brandId) {
        return ProductModel.of("커피", 1000L, SaleStatus.ON_SALE, "2026-01-01", brandId);
    }
    // 다양한 상품 리스트
    public static List<ProductModel> createProductList(Long brandId) {
        return List.of(
            ProductModel.of("사과", 3000L, SaleStatus.ON_SALE, "2026-01-01", brandId),
            ProductModel.of("바나나", 8000L, SaleStatus.ON_SALE, "2024-01-01", brandId),
            ProductModel.of("딸기", 2500L, SaleStatus.SOLD_OUT, "2020-01-01", brandId),
            ProductModel.of("수박", 1200L, SaleStatus.ON_SALE, "2025-01-05", brandId),
            ProductModel.of("포도", 1500L, SaleStatus.ON_SALE, "2025-05-05", brandId)
        );
    }

}
