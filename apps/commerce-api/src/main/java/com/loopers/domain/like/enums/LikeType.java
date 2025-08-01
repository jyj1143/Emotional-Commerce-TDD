package com.loopers.domain.like.enums;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.stream.Stream;

public enum LikeType {
    PRODUCT("상품")
    ;
    private final String description;

    LikeType(String description) {
        this.description = description;
    }

    public static LikeType from(String value) {
        if(value == null || value.isBlank()){
            throw new CoreException(ErrorType.BAD_REQUEST, "좋아요타입은 필수 값입니다.");
        }
        return Stream.of(LikeType.values())
            .filter(like -> like.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 좋아요타입 입니다. 입력값: " + value));
    }
}
