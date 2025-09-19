package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.repository.RankingInMemoryRepository;
import com.loopers.support.pagenation.Pageable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Redis를 이용한 상품 랭킹 조회 Repository
 * - Redis Sorted Set(ZSET)을 사용하여 날짜별 상품 점수 기반 랭킹 관리
 */
@Repository
@RequiredArgsConstructor
public class RankingRedisInMemoryRepository implements RankingInMemoryRepository {
    private final StringRedisTemplate redisTemplate;
    private static final String DATE_FORMAT_PATTERN = "yyyyMMdd";

    // Redis Key Prefix (실제 키: "ranking:product:all:20250912")
    private static final String PRODUCT_SCORE_KEY_PREFIX = "ranking:product:all:{date}";

    // DateTimeFormatter for LocalDate -> String 변환
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);


    private String generateRedisKey(LocalDate date) {
        return PRODUCT_SCORE_KEY_PREFIX.replace("{date}", date.format(FORMATTER));
    }

    /**
     * 특정 날짜의 상품 랭킹 리스트 조회
     */
    @Override
    public List<Long> getProductRanking(Pageable pageable, LocalDate date) {
        // Redis Key 생성: PRODUCT_SCORE_KEY_PREFIX에서 {date} 치환
        String key = generateRedisKey(date);

        int offset = pageable.getOffset();  // 시작 인덱스
        int limit = pageable.getLimit();    // 페이지 크기

        // ZSET에서 점수 기준 내림차순으로 offset ~ offset+limit-1 범위 조회
        return redisTemplate.opsForZSet()
            .reverseRange(key, offset, offset + limit - 1)
            .stream()
            .map(Long::valueOf)
            .toList();
    }

    /**
     * 특정 날짜 랭킹에 포함된 총 상품 수 조회
     */
    @Override
    public Long countRankedProducts(LocalDate date) {
        String key = generateRedisKey(date);

        // ZSET에 저장된 멤버 수 반환
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 특정 상품의 순위 조회
     */
    @Override
    public Long getRankOfProduct(Long productId, LocalDate date) {
        String key = generateRedisKey(date);
        // ZSET에서 점수 기준 내림차순 순위 조회 (0-based)
        Long rank = redisTemplate.opsForZSet().reverseRank(key, String.valueOf(productId));

        if (rank != null) {
            rank += 1; // 사용자에게 보여줄 때 1-based 순위로 변환
        }

        return rank;
    }

}
