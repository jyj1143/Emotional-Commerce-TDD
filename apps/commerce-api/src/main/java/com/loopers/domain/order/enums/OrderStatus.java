package com.loopers.domain.order.enums;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.stream.Stream;

public enum OrderStatus {
    PENDING("주문 접수"),
    PENDING_PAYMENT("결제 대기"),
    CANCELED("주문 취소"),
    ORDER_SUCCESS("주문 완료"),
    PAID("결제 완료"),
    SHIPPED("배송 시작"),
    DELIVERED("배송 완료"),
    ;

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public static OrderStatus from(String value) {
        if(value == null || value.isBlank()){
            throw new CoreException(ErrorType.BAD_REQUEST, "주문상태는 필수 값입니다.");
        }

        return Stream.of(OrderStatus.values())
            .filter(orderStatus -> orderStatus.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 주문상태 값입니다. 입력값: " + value));
    }
}
