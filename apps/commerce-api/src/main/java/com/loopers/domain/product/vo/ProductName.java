package com.loopers.domain.product.vo;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class ProductName {

    private static final int MAX_NAME_LENGTH = 15;
    private static final int MIN_NAME_LENGTH = 2;

    @Column(name = "name", nullable = false)
    private String name;

    private ProductName(String name) {
        checkLength(name);
        this.name = name;
    }

    public static ProductName of(String name) {
        return new ProductName(name);
    }

    protected void checkLength(String name) {
        if (name == null || name.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품명은 필수입니다.");
        }

        if (name.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품명은 비어있을 수 없습니다.");
        }

        if (name.length() < MIN_NAME_LENGTH) {
            throw new CoreException(ErrorType.BAD_REQUEST, String.format("상품명은 최소 %d글자 이상 입니다.", MIN_NAME_LENGTH));
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new CoreException(ErrorType.BAD_REQUEST, String.format("상품명은 최대 %d글자 이하 입니다.", MAX_NAME_LENGTH));
        }
    }
}
