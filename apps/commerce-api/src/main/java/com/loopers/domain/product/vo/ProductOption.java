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
public class ProductOption {

    private static final int MIN_NAME_LENGTH_TYPE = 2;
    private static final int MIN_NAME_LENGTH_VALUE = 2;

    @Column(name = "option_type", nullable = false)
    private String optionType; // 상품 옵션 타입(색상, 사이즈)
    @Column(name = "option_value", nullable = false)
    private String optionValue; // 상품 옵션 값(RED, BLUE, S, M, L 등)

    private ProductOption(String optionType, String optionValue) {
        checkLength(optionType, optionValue);
        this.optionType = optionType;
    }

    public static ProductOption of(String optionType, String optionValue) {
        return new ProductOption(optionType, optionValue);
    }

    protected void checkLength(String optionType, String optionValue) {
        if (optionType == null || optionValue == null
            || optionType.isEmpty() || optionValue.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "옵션정보는 필수입니다.");
        }

        if (optionType.isBlank() || optionValue.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "옵션정보는 비어있을 수 없습니다.");
        }

        if (optionType.length() < MIN_NAME_LENGTH_TYPE) {
            throw new CoreException(ErrorType.BAD_REQUEST, String.format("옵션타입은 최소 %d글자 이상 입니다.", MIN_NAME_LENGTH_TYPE));
        }

        if (optionValue.length() < MIN_NAME_LENGTH_VALUE) {
            throw new CoreException(ErrorType.BAD_REQUEST, String.format("옵션값은 최소 %d글자 이상 입니다.", MIN_NAME_LENGTH_VALUE));
        }
    }
}
