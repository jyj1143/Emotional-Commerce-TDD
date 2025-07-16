package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum Gender {
    MALE("남성"),
    FEMALE("여성"),
    ;
    private final String description;

    Gender(String description) {
        this.description = description;
    }

    public static Gender from(String value) {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별은 필수 값입니다");
        }

        return Stream.of(Gender.values())
            .filter(gender -> gender.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 성별 값입니다. 입력값: " + value));
    }
}
