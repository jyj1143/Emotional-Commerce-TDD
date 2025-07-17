package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointV1Dto.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1ApiController implements PointV1ApiSpec {

    @GetMapping
    @Override
    public ApiResponse<PointResponse> getMyPoint(@RequestHeader("X-USER-ID") String loginId) {
        PointResponse pointResponse = new PointResponse(loginId, 1000);
        return ApiResponse.success(pointResponse);
    }
}
