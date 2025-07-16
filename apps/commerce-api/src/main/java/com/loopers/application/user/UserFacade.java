package com.loopers.application.user;

import com.loopers.domain.user.LoginInfo;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserService;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.domain.user.dto.UserInfo;
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
}
