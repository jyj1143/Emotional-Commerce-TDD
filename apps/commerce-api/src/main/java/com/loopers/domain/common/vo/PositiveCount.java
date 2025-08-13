package com.loopers.domain.common.vo;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class PositiveCount {
    @Column(name = "count", nullable = false)
    private Long count;

    private PositiveCount(Long count) {
        if (count < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "0보다 작을 수 없습니다.");
        }
        this.count = count;
    }

    public static PositiveCount of(Long count) {
        return new PositiveCount(count);
    }

    public PositiveCount plus(Long other) {
        return new PositiveCount(this.count + other);
    }

    public PositiveCount minus(Long other) {
        return new PositiveCount(this.count - other);
    }
}
