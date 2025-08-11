package com.loopers.domain.coupone.vo;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class CouponPolicyName {
    private static final int MIN_NAME_LENGTH = 2;

    @Column(name = "name", nullable = false)
    private String name;

    private CouponPolicyName(String name) {
        checkLength(name);
        this.name = name;
    }

    public static CouponPolicyName of(String name) {
        return new CouponPolicyName(name);
    }

    protected void checkLength(String name) {
        if (name == null || name.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 정책명은 필수입니다.");
        }

        if (name.length() < MIN_NAME_LENGTH) {
            throw new CoreException(ErrorType.BAD_REQUEST, String.format("쿠폰 정책명은 최소 %d글자 이상 입니다.", MIN_NAME_LENGTH));
        }

        if (name.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 정책명은 비어있을 수 없습니다.");
        }
    }

}
