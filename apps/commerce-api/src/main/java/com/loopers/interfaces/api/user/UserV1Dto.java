package com.loopers.interfaces.api.user;

import com.loopers.domain.user.BirthDate;
import com.loopers.domain.user.Email;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.LoginInfo;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.domain.user.dto.UserInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class UserV1Dto {
    public record SignUpRequest(
        @NotEmpty
        String loginId,
        @NotBlank(message = "성별은 필수입니다.")
        String gender,
        @NotEmpty
        String birthDate,
        @NotEmpty
        String email
        ){

        public UserCommand.Create toCommand() {
            return new UserCommand.Create(
                new LoginInfo(loginId),
                new Email(email),
                Gender.from(gender),
                new BirthDate(birthDate)
            );
        }

    }

    public record SignUpResponse(
        String loginId,
        String gender,
        String birthDate,
        String email
    ){
        public static UserV1Dto.SignUpResponse from(UserInfo userInfo) {
            return new UserV1Dto.SignUpResponse(
                userInfo.loginInfo().getLoginId(),
                userInfo.gender().name(),
                userInfo.birthDate().getBirthDate().toString(),
                userInfo.email().getEmail()
            );
        }
    }

    public record UserResponse(
        String loginId,
        String gender,
        String birthDate,
        String email
    ){
        public static UserResponse from(UserInfo userInfo) {
            return new UserResponse(
                userInfo.loginInfo().getLoginId(),
                userInfo.gender().name(),
                userInfo.birthDate().getBirthDate().toString(),
                userInfo.email().getEmail()
            );
        }
    }
}
