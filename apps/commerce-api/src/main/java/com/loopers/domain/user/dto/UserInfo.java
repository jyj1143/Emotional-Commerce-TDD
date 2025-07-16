package com.loopers.domain.user.dto;

import com.loopers.domain.user.BirthDate;
import com.loopers.domain.user.Email;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.LoginInfo;
import com.loopers.domain.user.UserModel;

public record UserInfo(LoginInfo loginInfo,
                       Email email,
                       Gender gender,
                       BirthDate birthDate) {

    public static UserInfo from(UserModel user) {
        return new UserInfo(
            user.getLoginInfo(),
            user.getEmail(),
            user.getGender(),
            user.getBirthDate()
        );
    }
}
