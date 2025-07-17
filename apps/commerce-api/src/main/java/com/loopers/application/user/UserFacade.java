package com.loopers.application.user;

import com.loopers.domain.user.LoginInfo;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserService;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.domain.user.dto.UserInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;

    @Transactional
    public UserInfo signUp(UserCommand.Create command) {
        UserModel user = userService.signUp(command);
        return UserInfo.from(user);
    }

    public UserInfo getMyInfo(String loginId) {
        LoginInfo loginInfo = new LoginInfo(loginId);
        UserModel user = userService.getUser(loginInfo);
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        return UserInfo.from(user);
    }
}
