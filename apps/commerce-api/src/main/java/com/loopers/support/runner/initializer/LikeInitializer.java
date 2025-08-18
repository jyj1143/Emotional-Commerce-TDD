package com.loopers.support.runner.initializer;

import com.loopers.support.runner.generator.LikeDataGenerator;
import com.loopers.support.runner.threding.ExecutorServiceUtils;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeInitializer {
    private final JdbcTemplate jdbcTemplate;
    private static final int THREAD_COUNT = 4;
    private static final int BATCH_SIZE = 1_000;
    private static final int PRODUCT_COUNT = 100_000;
    private static final int USER_COUNT = 500_000;
    private static final int LIKE_COUNT = 1_000_000;
    private static final String LIKE_INSERT_SQL =
        "INSERT INTO likes (id, ref_user_id, ref_target_id, like_type, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at)";
    
    // 사용자별 좋아요한 상품 목록을 추적하기 위한 맵
    private final Map<Long, Set<Long>> userLikesMap = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    // 일부 인기 상품 - 많은 사용자가 좋아요할 확률이 높은 상품
    private final List<Long> popularProducts = new ArrayList<>();
    
    public void bulkInsertLikesWithMultiThreading() {
        // 인기 상품 초기화 (상위 5% 상품을 인기 상품으로 지정)
        for (int i = 1; i <= PRODUCT_COUNT * 0.05; i++) {
            popularProducts.add((long) i);
        }
        
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        int batchCount = LIKE_COUNT / BATCH_SIZE;

        try {
            for (int i = 0; i < batchCount; i++) {
                final long batchNumber = i;
                executorService.submit(() -> insertLikeBatch(batchNumber));
            }
        } finally {
            ExecutorServiceUtils.shutdownGracefully(executorService, 60, TimeUnit.SECONDS);
        }
    }

    private void insertLikeBatch(long batchNumber) {
        List<Object[]> batchParams = new ArrayList<>();
        long startId = batchNumber * BATCH_SIZE + 1;
        long endId = startId + BATCH_SIZE;
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        for (long id = startId; id < endId; id++) {
            Object[] likeData = createLikeData(id, now);
            batchParams.add(likeData);
        }

        try {
            jdbcTemplate.batchUpdate(LIKE_INSERT_SQL, batchParams);
            log.debug("Inserted like batch: {} - {}", startId, endId - 1);
        } catch (Exception e) {
            log.error("Failed to insert like batch: {} - {}", startId, endId - 1, e);
        }
    }

    private Object[] createLikeData(long id, Timestamp now) {
        LikeDataGenerator dataGenerator = new LikeDataGenerator();
        long refUserId;
        long refTargetId;
        
        // 사용자 선택 로직
        if (random.nextDouble() < 0.7) {
            // 70% 확률로 기존 사용자가 다시 좋아요
            refUserId = (id % (USER_COUNT / 5)) + 1;
        } else {
            // 30% 확률로 새로운 사용자
            refUserId = dataGenerator.generateRandomId(USER_COUNT);
        }
        
        // 사용자의 좋아요 목록 가져오기
        Set<Long> userLikes = userLikesMap.computeIfAbsent(refUserId, k -> new HashSet<>());
        
        // 상품 선택 로직
        if (random.nextDouble() < 0.3 && !popularProducts.isEmpty()) {
            // 30% 확률로 인기 상품에 좋아요
            refTargetId = popularProducts.get(random.nextInt(popularProducts.size()));
        } else {
            // 70% 확률로 랜덤 상품
            // 파레토 분포를 적용 - 80/20 법칙 (80%의 좋아요가 20%의 상품에 집중)
            double paretoValue = Math.pow(random.nextDouble(), 0.2); // 파레토 분포 파라미터
            refTargetId = (long) (paretoValue * PRODUCT_COUNT) + 1;
        }
        
        // 이미 좋아요한 상품이면 다른 상품 찾기
        while (userLikes.contains(refTargetId)) {
            refTargetId = dataGenerator.generateRandomId(PRODUCT_COUNT);
        }
        
        // 사용자의 좋아요 목록에 추가
        userLikes.add(refTargetId);
        
        String likeType = "PRODUCT";
        return new Object[]{id, refUserId, refTargetId, likeType, now, now};
    }
}
