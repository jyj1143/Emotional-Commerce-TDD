package com.loopers.interfaces.api.point;

import com.loopers.domain.user.dto.UserInfo;

public class PointV1Dto {
    public record PointResponse(
        String loginId,
        Long amount
    ){
        public static PointV1Dto.PointResponse from(UserInfo userInfo) {
            return new PointV1Dto.PointResponse(
                userInfo.loginInfo().getLoginId(),
                userInfo.point()
            );
        }
    }

    public record ChargeRequest(
        String loginId,
        Long amount
    ){

    }
}
