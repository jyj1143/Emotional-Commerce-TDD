package com.loopers.domain.point.service.dto;

import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.vo.Amount;

public record PointInfo(
    Long id,
    Long userId,
    Amount amount
) {

    public static PointInfo from(PointModel point) {
        return new PointInfo(
            point.getId(),
            point.getRefUserId(),
            point.getAmount()
        );
    }
}
