package com.loopers.support.runner.initializer;

import com.loopers.support.runner.generator.ProductDataGenerator;
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
public class ProductInitializer {
    private final JdbcTemplate jdbcTemplate;
    private static final int THREAD_COUNT = 4;
    private static final int BATCH_SIZE = 1_000;
    private static final int BRAND_COUNT = 1_000;
    private static final int PRODUCT_COUNT = 1_000_000;
    private static final String PRODUCT_INSERT_SQL =
        "INSERT INTO product (id, name, sale_price, sale_status, sale_date, ref_brand_id, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public void bulkInsertProductsWithMultiThreading() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        int batchCount = PRODUCT_COUNT / BATCH_SIZE;

        try {
            for (int i = 0; i < batchCount; i++) {
                final long batchNumber = i;
                executorService.submit(() -> insertProductBatch(batchNumber));
            }
        } finally {
            ExecutorServiceUtils.shutdownGracefully(executorService, 60, TimeUnit.SECONDS);
        }
    }

    private void insertProductBatch(long batchNumber) {
        List<Object[]> batchParams = new ArrayList<>();
        long startId = batchNumber * BATCH_SIZE + 1;
        long endId = startId + BATCH_SIZE;
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        for (long id = startId; id < endId; id++) {
            Object[] productData = createProductData(id, now);
            batchParams.add(productData);
        }

        try {
            jdbcTemplate.batchUpdate(PRODUCT_INSERT_SQL, batchParams);
            log.debug("Inserted product batch: {} - {}", startId, endId - 1);
        } catch (Exception e) {
            log.error("Failed to insert product batch: {} - {}", startId, endId - 1, e);
        }
    }

    private Object[] createProductData(long id, Timestamp now) {
        ProductDataGenerator dataGenerator = new ProductDataGenerator();
        return new Object[] {
            id,
            "Product " + id,
            dataGenerator.generateRandomPrice(),
            dataGenerator.generateRandomSaleStatus(),
            dataGenerator.generateRandomSaleDate(),
            dataGenerator.generateRandomId(BRAND_COUNT),
            now,
            now
        };
    }
}
