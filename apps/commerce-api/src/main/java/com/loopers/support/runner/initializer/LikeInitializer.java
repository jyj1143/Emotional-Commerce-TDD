package com.loopers.support.runner.initializer;

import com.loopers.support.runner.generator.LikeDataGenerator;
import com.loopers.support.runner.threding.ExecutorServiceUtils;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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


    public void bulkInsertLikesWithMultiThreading() {
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
            Object[]  likeData = createLikeData(id, now);
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
        long refUserId = dataGenerator.generateRandomId(USER_COUNT);
        long refTargetId = dataGenerator.generateRandomId(PRODUCT_COUNT);
        String likeType = "PRODUCT";

        return new Object[]{id, refUserId, refTargetId, likeType, now, now};
    }
}
