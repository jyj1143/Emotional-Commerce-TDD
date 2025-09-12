package com.loopers.application.ranking;

import com.loopers.application.ranking.dto.RankingCriteria;
import com.loopers.application.ranking.dto.RankingResult;
import com.loopers.domain.brand.dto.BrandInfo;
import com.loopers.domain.brand.service.BrandService;
import com.loopers.domain.product.dto.product.ProductInfo;
import com.loopers.domain.product.service.ProductService;
import com.loopers.domain.ranking.RankingService;
import com.loopers.domain.ranking.dto.RankingInfo;
import com.loopers.support.pagenation.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingFacade {

    private final RankingService rankingService;
    private final ProductService productService;
    private final BrandService brandService;

    public PageResult<RankingResult> searchRankings(RankingCriteria.SearchRankings criteria) {

        PageResult<RankingInfo> productRanking = rankingService.getProductRanking(criteria.toCommand());

        return productRanking.map(ranking -> {
            ProductInfo product = productService.get(ranking.productId());
            BrandInfo brand = brandService.get(product.brandId());
            return new RankingResult(
                product.id(),
                product.name(),
                product.price(),
                product.saleStatus().name(),
                brand.id(),
                brand.name(),
                ranking.rank()
            );
        });
    }
}
