package com.loopers.application.product;

import com.loopers.application.product.dto.ProductCriteria;
import com.loopers.application.product.dto.ProductResult;
import com.loopers.application.product.dto.ProductSummaryResult;
import com.loopers.domain.brand.entity.BrandModel;
import com.loopers.domain.brand.service.BrandService;
import com.loopers.domain.like.enums.LikeType;
import com.loopers.domain.like.service.LikeService;
import com.loopers.domain.product.cache.ProductCacheRepository;
import com.loopers.domain.product.dto.product.ProductCommand;
import com.loopers.domain.product.dto.summary.ProductSummaryInfo;
import com.loopers.domain.product.entity.ProductModel;
import com.loopers.domain.product.service.ProductService;
import com.loopers.domain.product.service.ProductSummaryService;
import com.loopers.application.product.dto.ProductInfo;
import com.loopers.support.pagenation.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final ProductSummaryService productSummaryService;
    private final BrandService brandService;
    private final LikeService likeService;
    private final ProductCacheRepository productCacheRepository;

    /**
     * 상품 상세 조회
     * 1. 캐시에서 조회
     * 2. 캐시 없으면 DB에서 조회 후 캐시 업데이트
     */
    public ProductResult getProductDetail(ProductCriteria.GetProduct criteria) {
        ProductInfo productDetails = productCacheRepository.findProductDetail(criteria.productId())
            .orElseGet(() -> {
                ProductModel product = productService.get(criteria.productId(), criteria.userId());
                BrandModel brand = brandService.get(product.getRefBrandId());
                Long likeCount = likeService.count(criteria.productId(), LikeType.PRODUCT);

                ProductInfo productInfo = ProductInfo.of(product, brand, likeCount);

                productCacheRepository.saveProductDetail(criteria.productId(), productInfo);

                return productInfo;
            });

        return ProductResult.of(productDetails);
    }

    /**
     * 상품 목록 조회
     * 1. 캐시에서 조회
     * 2. 캐시 없으면 DB에서 조회 후 캐시 업데이트
     */
    public PageResult<ProductSummaryResult> findAllProductSummary(
        ProductCriteria.GetProductSummary criteria
    ) {
        ProductCommand.ProductSummary command = criteria.toProductCommand();

        PageResult<ProductSummaryInfo> productSummaryPageResult =
            productCacheRepository.findProductSummary(command)
                .orElseGet(() -> {
                    // 캐시 미스 시 DB 조회
                    PageResult<ProductSummaryInfo> result =
                        productSummaryService.findAll(command);

                    // 조회 결과를 캐시에 저장
                    productCacheRepository.saveProductSummary(command, result);

                    return result;
                });

        return new PageResult<>(
            productSummaryPageResult.content().stream()
                .map(ProductSummaryResult::from)
                .toList(),
            productSummaryPageResult.paginationInfo()
        );
    }

}
