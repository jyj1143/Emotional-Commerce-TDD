package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Point V1 API", description = "Point management API for version 1")
public interface PointV1ApiSpec {
    @Operation(
        summary = "포인트 조회",
        description = "회원 포인트를 조회합니다."
    )
    ApiResponse<PointV1Dto.PointResponse> getMyPoint(
        String loginId
    );

    @Operation(
        summary = "포인트 충전",
        description = "포인트를 충전합니다."
    )
    ApiResponse<PointV1Dto.PointResponse> chargePoint(
        String loginId,
        PointV1Dto.ChargeRequest pointResponse
    );
}
