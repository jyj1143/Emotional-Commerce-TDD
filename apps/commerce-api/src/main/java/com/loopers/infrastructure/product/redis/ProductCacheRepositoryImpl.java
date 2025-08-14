package com.loopers.infrastructure.product.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.product.dto.ProductInfo;
import com.loopers.application.product.dto.ProductResult;
import com.loopers.domain.product.cache.ProductCacheRepository;
import com.loopers.domain.product.dto.product.ProductCommand;
import com.loopers.domain.product.dto.summary.ProductSummaryInfo;
import com.loopers.support.pagenation.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductCacheRepositoryImpl implements ProductCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PRODUCT_DETAIL_KEY_PREFIX = "product:detail:";
    private static final String PRODUCT_SUMMARY_KEY_PREFIX = "product:summary:";
    private static final long CACHE_TTL_SECONDS = 600; // 10분

    @Override
    public Optional<ProductInfo> findProductDetail(Long productId) {
        String key = generateProductDetailKey(productId);
        String cachedJson = redisTemplate.opsForValue().get(key);

        if (cachedJson == null) {
            return Optional.empty();
        }

        try {
            ProductInfo productResult = objectMapper.readValue(cachedJson, ProductInfo.class);
            return Optional.of(productResult);
        } catch (JsonProcessingException e) {
            log.error("상품 상세 정보 역직렬화 실패: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public void saveProductDetail(Long productId, ProductInfo productInfo) {
        String key = generateProductDetailKey(productId);
        ProductResult productResult = ProductResult.of(productInfo);

        try {
            String productJson = objectMapper.writeValueAsString(productResult);
            redisTemplate.opsForValue().set(key, productJson, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            log.error("상품 상세 정보 직렬화 실패: {}", e.getMessage(), e);
        }
    }

    @Override
    public Optional<PageResult<ProductSummaryInfo>> findProductSummary(ProductCommand.ProductSummary criteria) {
        String key = generateSummaryKey(criteria);
        String cachedJson = redisTemplate.opsForValue().get(key);

        if (cachedJson == null) {
            return Optional.empty();
        }

        try {
            PageResult<ProductSummaryInfo> result = objectMapper.readValue(
                cachedJson,
                objectMapper.getTypeFactory().constructParametricType(
                    PageResult.class,
                    ProductSummaryInfo.class
                )
            );
            return Optional.of(result);
        } catch (JsonProcessingException e) {
            log.error("상품 요약 정보 역직렬화 실패: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public void saveProductSummary(ProductCommand.ProductSummary criteria, PageResult<ProductSummaryInfo> summaryInfo) {
        String key = generateSummaryKey(criteria);

        try {
            String summaryJson = objectMapper.writeValueAsString(summaryInfo);
            redisTemplate.opsForValue().set(key, summaryJson, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            log.error("상품 요약 정보 직렬화 실패: {}", e.getMessage(), e);
        }
    }

    @Override
    public String generateProductDetailKey(Long productId) {
        return PRODUCT_DETAIL_KEY_PREFIX + productId;
    }

    @Override
    public String generateSummaryKey(ProductCommand.ProductSummary criteria) {
        StringBuilder keyBuilder = new StringBuilder(PRODUCT_SUMMARY_KEY_PREFIX);

        if (criteria.sortType() != null) {
            keyBuilder.append("sort-").append(criteria.sortType()).append(":");
        }

        if (criteria.sortOrder() != null) {
            keyBuilder.append("order-").append(criteria.sortOrder()).append(":");
        }

        keyBuilder.append("page:").append(criteria.page())
            .append(":size:").append(criteria.size());

        return keyBuilder.toString();
    }
}
