package com.loopers.domain.ranking.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 제품 랭킹에 사용되는 사용자 활동별 가중치를 정의하는 열거형
 */
@Getter
@RequiredArgsConstructor
public enum ProductRankingWeight {
    VIEWED(1.0),
    LIKED(2.0),
    ORDERED(6.0),
    UNKNOWN(0.0);

    private final double weight;

    public static double getWeight(String action) {
        try {
            return valueOf(action).weight;
        } catch (IllegalArgumentException | NullPointerException e) {
            return UNKNOWN.weight;
        }
    }

    public static ProductRankingWeight fromString(String action) {
        try {
            return valueOf(action);
        } catch (IllegalArgumentException | NullPointerException e) {
            return UNKNOWN;
        }
    }
}

