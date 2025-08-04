package com.loopers.domain.coupone.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class UsagePeriod {

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt; // 발행 시간

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime; // 만료 시간

    private UsagePeriod(LocalDateTime issuedAt, LocalDateTime expirationTime) {
        if (issuedAt == null || expirationTime == null) {
            throw new IllegalArgumentException("발행 시간과 만료 시간은 필수입니다.");
        }
        if (issuedAt.isAfter(expirationTime)) {
            throw new IllegalArgumentException("발행 시간은 만료 시간보다 이후일 수 없습니다.");
        }

        this.issuedAt = issuedAt;
        this.expirationTime = expirationTime;
    }

    public static UsagePeriod of(LocalDateTime issuedAt, LocalDateTime expirationTime) {
        return new UsagePeriod(issuedAt, expirationTime);
    }

}
