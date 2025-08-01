package com.loopers.domain.point.service.dto;

public class PointCommand {
    public record Create(
        Long userId,
        Long point
    ) {
    }

    public record ChargePoint(
        Long userId,
        Long point
    ) {
    }

    public record UsePoint(
        Long userId,
        Long point
    ) {
    }

    public record GetPoint(
        Long userId
    ) {
    }
}
