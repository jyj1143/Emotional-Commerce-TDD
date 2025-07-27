package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Point {
    @Column(name = "point", nullable = false)
    Long point;

    public Point(Long point) {
        if (point == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트는 비어있을 수 없습니다.");
        }
        if (point < 0L) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트는 음수일 수 없습니다.");
        }
        this.point = point;
    }
}
