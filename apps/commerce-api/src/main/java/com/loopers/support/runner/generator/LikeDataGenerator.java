package com.loopers.support.runner.generator;

import java.util.Random;

public class LikeDataGenerator {
    private final Random random = new Random();

    public long generateRandomId(int bound) {
        return random.nextInt(bound) + 1;
    }

}
