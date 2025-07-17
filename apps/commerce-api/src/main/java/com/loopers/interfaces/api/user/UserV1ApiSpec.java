package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User V1 API", description = "User management API for version 1")
public interface UserV1ApiSpec {
    @Operation(
        summary = "회원가입",
        description = "회원가입"
    )
    ApiResponse<UserV1Dto.SignUpResponse> signUp(
        @Schema(name = "회원가입 입력 요청", description = "회원가입 입력 정보")
        UserV1Dto.SignUpRequest signUpRequest
    );

    @Operation(
        summary = "내 정보 조회",
        description = "내 정보를 조회합니다."
    )
    ApiResponse<UserV1Dto.UserResponse> getMyInfo(
        String loginId
    );
}
