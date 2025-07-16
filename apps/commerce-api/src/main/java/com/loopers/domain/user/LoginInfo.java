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
public class LoginInfo {

    private final String PATTERN_LOGIN_ID = "^[a-zA-Z0-9]{1,10}$";

    @Column(name = "login_id", nullable = false, unique = true, length = 10)
    String loginId;

    public LoginInfo(String loginId) {
        if(loginId == null || loginId.isBlank()){
            throw new CoreException(ErrorType.BAD_REQUEST, "로그인ID는 비어있을 수 없습니다.");
        }
        if (loginId == null || !loginId.matches(PATTERN_LOGIN_ID)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 로그인ID 형식입니다.");
        }
        this.loginId = loginId;
    }

}
