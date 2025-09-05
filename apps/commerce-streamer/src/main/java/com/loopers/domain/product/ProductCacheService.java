package com.loopers.domain.product;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCacheService {

    private static final String PRODUCT_DETAIL_PREFIX = "product.detail:";

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 재고가 0인 상품의 캐시를 무효화합니다.
     */
    public void invalidateCacheForZeroStock(Long productSkuId) {
        String cacheKey = PRODUCT_DETAIL_PREFIX + productSkuId;
        Boolean deleted = redisTemplate.delete(cacheKey);
        log.info("캐시 무효화 완료: {} (성공: {})", cacheKey, deleted);
    }

}
