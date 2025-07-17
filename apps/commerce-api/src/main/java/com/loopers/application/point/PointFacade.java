package com.loopers.application.point;

import com.loopers.domain.user.LoginInfo;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserService;
import com.loopers.domain.user.dto.UserInfo;
import com.loopers.interfaces.api.point.PointV1Dto.ChargeRequest;
import com.loopers.interfaces.api.point.PointV1Dto.PointResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class PointFacade {
    private final UserService userService;

    public PointResponse getPoint(String loginId) {
        LoginInfo loginInfo = new LoginInfo(loginId);
        UserModel user = userService.getUser(loginInfo);
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        UserInfo userInfo = UserInfo.from(user);
        return PointResponse.from(userInfo);
    }

    @Transactional
    public PointResponse chargePoint(ChargeRequest chargeRequest) {
        LoginInfo loginInfo = new LoginInfo(chargeRequest.loginId());
        UserModel user = userService.getUser(loginInfo);
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        UserModel userModel = userService.addPoint(loginInfo, chargeRequest.amount());
        UserInfo userInfo = UserInfo.from(userModel);
        return PointResponse.from(userInfo);
    }

}
