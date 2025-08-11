package com.loopers.support.runner.generator;

import java.sql.Timestamp;
import java.util.Random;

public class ProductDataGenerator {

    private final Random random = new Random();

    private static final String[] SALE_STATUSES = {"ON_SALE", "SOLD_OUT", "STOP_SALE"};
    private static final long ONE_YEAR_IN_MILLIS = 365L * 24 * 60 * 60 * 1000;
    private static final int MIN_PRICE_MULTIPLIER = 1;
    private static final int MAX_PRICE_RANGE = 990;
    private static final long BASE_PRICE = 1_000L;

    public long generateRandomId(int bound) {
        return random.nextInt(bound) + 1;
    }

    public long generateRandomPrice() {
        return BASE_PRICE * (MIN_PRICE_MULTIPLIER + random.nextInt(MAX_PRICE_RANGE));
    }

    public String generateRandomSaleStatus() {
        return SALE_STATUSES[random.nextInt(SALE_STATUSES.length)];
    }

    public Timestamp generateRandomSaleDate() {
        long currentTime = System.currentTimeMillis();
        // 현재 시간 기준으로 1년 전/후 범위에서 랜덤 날짜 생성
        long randomTime = currentTime + (long) ((random.nextDouble() * 2 - 1) * ONE_YEAR_IN_MILLIS);
        return new Timestamp(randomTime);
    }
}

