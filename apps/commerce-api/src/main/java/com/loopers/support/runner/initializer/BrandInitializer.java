package com.loopers.support.runner.initializer;

import com.loopers.support.runner.threding.ExecutorServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandInitializer {
    private final JdbcTemplate jdbcTemplate;
    private static final int THREAD_COUNT = 4;
    private static final int BATCH_SIZE = 1_000;
    private static final int BRAND_COUNT = 1_000;
    private static final String BRAND_INSERT_SQL =
            "INSERT INTO brand (id, name, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?)";


    public void bulkInsertBrandsWithMultiThreading() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        int batchCount = BRAND_COUNT / BATCH_SIZE;

        try {
            for (int i = 0; i < batchCount; i++) {
                final long batchNumber = i;
                executorService.submit(() -> insertBrandBatch(batchNumber));
            }
        } finally {
            ExecutorServiceUtils.shutdownGracefully(executorService, 60, TimeUnit.SECONDS);
        }
    }

    private void insertBrandBatch(long batchNumber) {
        List<Object[]> batchParams = new ArrayList<>();
        long startId = batchNumber * BATCH_SIZE + 1;
        long endId = startId + BATCH_SIZE;
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        for (long id = startId; id < endId; id++) {
            Object[] brandData = createBrandData(id, now);
            batchParams.add(brandData);
        }

        try {
            jdbcTemplate.batchUpdate(BRAND_INSERT_SQL, batchParams);
            log.debug("Inserted brand batch: {} - {}", startId, endId - 1);
        } catch (Exception e) {
            log.error("Failed to insert brand batch: {} - {}", startId, endId - 1, e);
        }
    }


    private Object[] createBrandData(long id, Timestamp now) {
        return new Object[] {
                id,
                "Brand " + id,
                now,
                now
        };
    }

}


