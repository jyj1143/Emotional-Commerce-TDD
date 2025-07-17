package com.loopers.application.point;

import com.loopers.domain.user.LoginInfo;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserService;
import com.loopers.domain.user.dto.UserInfo;
import com.loopers.interfaces.api.point.PointV1Dto.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointFacade {
    private final UserService userService;

    public PointResponse getPoint(String loginId) {
        LoginInfo loginInfo = new LoginInfo(loginId);
        UserModel user = userService.getUser(loginInfo);
        if (user == null) {
            return null;
        }
        UserInfo userInfo = UserInfo.from(user);
        return PointResponse.from(userInfo);
    }
}
