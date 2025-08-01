package com.loopers.domain.brand.entity;


import java.util.List;

public class BrandFixture {
    // 단일 브랜드
    public static BrandModel createBrand() {
        return BrandModel.of("나이키");
    }
    // 다중 브랜드
    public static List<BrandModel> createBrandList() {
        return List.of(
            BrandModel.of("나이키"),
            BrandModel.of("아디다스"),
            BrandModel.of("푸마"),
            BrandModel.of("뉴발란스"),
            BrandModel.of("언더아머")
        );
    }
}
