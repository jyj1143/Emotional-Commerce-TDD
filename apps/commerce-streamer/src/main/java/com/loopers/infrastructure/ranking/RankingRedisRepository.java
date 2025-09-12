package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.enums.ProductRankingWeight;
import com.loopers.domain.ranking.repository.RankingRepository;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.zset.Aggregate;
import org.springframework.data.redis.connection.zset.Weights;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RankingRedisRepository implements RankingRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String DATE_FORMAT_PATTERN = "yyyyMMdd";

    private static final String PRODUCT_SCORE_KEY_PREFIX = "ranking:product:all:{date}";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

    /**
     * 현재 날짜 기반 Redis 키를 생성
     */
    private String generateRedisKey() {
        return PRODUCT_SCORE_KEY_PREFIX.replace("{date}", ZonedDateTime.now().format(FORMATTER));
    }

    /**
     * 상품의 점수를 지정된 가중치로 증가
     */
    private void incrementScore(Long productId, double weight) {
        String key = generateRedisKey();
        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(productId), weight);
    }

    public void scoreView(Long productId) {
        incrementScore(productId, ProductRankingWeight.VIEWED.getWeight());
    }

    public void scoreLike(Long productId) {
        incrementScore(productId, ProductRankingWeight.LIKED.getWeight());
    }

    public void scoreUnLike(Long productId) {
        incrementScore(productId, ProductRankingWeight.LIKED.getWeight() * -1);
    }

    public void scoreOrder(Long productId) {
        incrementScore(productId, ProductRankingWeight.ORDERED.getWeight());
    }

    /**
     * 오늘의 랭킹 점수를 일부(10%) 내일 랭킹에 이월
     * - 오늘 점수는 점차 소멸되고, 내일 랭킹에 누적 반영
     * - 내일 랭킹 키는 2일 동안만 유지
     */
    public void carryOver() {
        String todayKey = PRODUCT_SCORE_KEY_PREFIX + ZonedDateTime.now().format(FORMATTER);
        String tomorrowKey = PRODUCT_SCORE_KEY_PREFIX + ZonedDateTime.now().plusDays(1).format(FORMATTER);

        // 오늘 랭킹 점수를 내일 랭킹으로 이월 (0.1 가중치 적용)
        redisTemplate.opsForZSet()
            .unionAndStore(
                todayKey,
                Collections.emptyList(),
                tomorrowKey,
                Aggregate.SUM,
                Weights.of(0.1)
            );

        // 만료 시간 설정
        redisTemplate.expire(tomorrowKey, Duration.ofDays(2)); // 내일 랭킹은 이틀 동안 유지
        redisTemplate.expire(todayKey, Duration.ofDays(1));   // 오늘 랭킹은 하루만 유지
    }

}
