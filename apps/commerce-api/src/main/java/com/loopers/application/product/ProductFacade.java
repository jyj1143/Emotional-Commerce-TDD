package com.loopers.application.product;

import com.loopers.application.product.dto.ProductInfo;
import com.loopers.domain.brand.entity.BrandModel;
import com.loopers.domain.brand.service.BrandService;
import com.loopers.domain.like.enums.LikeType;
import com.loopers.domain.like.service.LikeService;
import com.loopers.domain.product.entity.ProductModel;
import com.loopers.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final BrandService brandService;
    private final LikeService likeService;

    public ProductInfo getProductDetail(Long productId) {
        ProductModel product = productService.get(productId);
        BrandModel brand = brandService.get(product.getRefBrandId());
        Long likeCount = likeService.count(productId, LikeType.PRODUCT);
        return ProductInfo.of(product, brand, likeCount);
    }

}
