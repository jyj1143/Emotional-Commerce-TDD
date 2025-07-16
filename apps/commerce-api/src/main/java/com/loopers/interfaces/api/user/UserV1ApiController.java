package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserFacade;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.domain.user.dto.UserInfo;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.user.UserV1Dto.SignUpRequest;
import com.loopers.interfaces.api.user.UserV1Dto.SignUpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserV1ApiController implements UserV1ApiSpec {
    
    private final UserFacade userFacade;
    
    @PostMapping
    @Override
    public ApiResponse<SignUpResponse> signUp(
        @Valid @RequestBody SignUpRequest signUpRequest
    ) {
        UserCommand.Create command = signUpRequest.toCommand();
        UserInfo userInfo = userFacade.signUp(command);

        SignUpResponse signUpResponse = SignUpResponse.from(userInfo);
        return ApiResponse.success(signUpResponse);
    }
}
