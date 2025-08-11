package com.loopers.domain.coupone.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class CouponPeriod {

    @Column(nullable = false)
    private LocalDateTime startTime; // 발행 시작 시간

    @Column(nullable = false)
    private LocalDateTime endTime; // 발행 종료 시간

    private CouponPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("시작 시간과 종료 시간은 필수입니다.");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 이후일 수 없습니다.");
        }

        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static CouponPeriod of(LocalDateTime startTime, LocalDateTime endTime) {
        return new CouponPeriod(startTime, endTime);
    }

}
