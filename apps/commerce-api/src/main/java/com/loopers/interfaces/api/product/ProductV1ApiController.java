package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.dto.ProductCriteria.GetProduct;
import com.loopers.application.product.dto.ProductResult;
import com.loopers.application.product.dto.ProductSummaryResult;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.ProductV1Dto.GetProductSummaryRequest;
import com.loopers.interfaces.api.product.ProductV1Dto.GetResponse;
import com.loopers.interfaces.api.product.ProductV1Dto.SearchProductSummaryResponse;
import com.loopers.support.pagenation.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductV1ApiController implements ProductV1ApiSpec{

    private final ProductFacade productFacade;

    @GetMapping
    @Override
    public ApiResponse<SearchProductSummaryResponse> searchProducts(
        GetProductSummaryRequest request) {
        PageResult<ProductSummaryResult> productSummary = productFacade.findAllProductSummary(request.toProductCriteria());
        return ApiResponse.success(SearchProductSummaryResponse.from(productSummary));
    }

    @GetMapping("/{productId}")
    @Override
    public ApiResponse<GetResponse> getProductDetail(Long productId) {
        ProductResult productDetail = productFacade.getProductDetail(
            new GetProduct(productId)
        );
        return ApiResponse.success(ProductV1Dto.GetResponse.from(productDetail));
    }
}
