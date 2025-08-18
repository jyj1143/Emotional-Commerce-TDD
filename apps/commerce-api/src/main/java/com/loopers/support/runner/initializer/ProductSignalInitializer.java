package com.loopers.support.runner.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSignalInitializer {
    private final JdbcTemplate jdbcTemplate;
    private static final String PRODUCT_SIGNAL_UPSERT_SQL =
            "INSERT INTO product_signal (ref_product_id, like_count, created_at, updated_at) " +
                    "SELECT ref_target_id, COUNT(*), NOW(), NOW() " +
                    "FROM likes " +
                    "WHERE like_type = 'PRODUCT' " +
                    "GROUP BY ref_target_id " +
                    "ON DUPLICATE KEY UPDATE " +
                    "like_count = VALUES(like_count), " +
                    "updated_at = VALUES(updated_at)";

    public void aggregateAndInsertProductSignals() {
        try {
            int updatedRows = jdbcTemplate.update(PRODUCT_SIGNAL_UPSERT_SQL);
            log.info("Product signal aggregation completed. Updated {} products", updatedRows);
        } catch (Exception e) {
            log.error("Failed to aggregate product signals", e);
        }
    }
}
