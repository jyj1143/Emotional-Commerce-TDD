package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointV1Dto.PointResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1ApiController implements PointV1ApiSpec {

    private final PointFacade pointFacade;

    @GetMapping
    @Override
    public ApiResponse<PointResponse> getMyPoint(@RequestHeader("X-USER-ID") String loginId) {
        PointResponse point = pointFacade.getPoint(loginId);
        return ApiResponse.success(point);
    }

    @PostMapping
    @Override
    public ApiResponse<PointResponse> chargePoint(
        @RequestHeader("X-USER-ID") String loginId,
        @RequestBody PointV1Dto.ChargeRequest chargeRequest) {

        if("test".equals(loginId)) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 회원입니다.");
        }
        PointResponse pointResponse = new PointResponse(loginId, chargeRequest.amount());
        return ApiResponse.success(pointResponse);
    }
}
