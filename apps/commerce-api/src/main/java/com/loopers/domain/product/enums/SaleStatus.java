package com.loopers.domain.product.enums;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum SaleStatus {
    ON_SALE("판매중"),
    SOLD_OUT("품절"),
    STOP_SALE("단종");

    private final String description;

    SaleStatus(String description) {
        this.description = description;
    }

    public static SaleStatus from(String value) {
       if(value == null || value.isBlank()){
           throw new CoreException(ErrorType.BAD_REQUEST, "판매상태는 필수 값입니다.");
       }

        return Stream.of(SaleStatus.values())
            .filter(saleStatus -> saleStatus.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 판매상태 값입니다. 입력값: " + value));
    }

}
