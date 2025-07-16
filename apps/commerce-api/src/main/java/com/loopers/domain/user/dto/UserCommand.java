package com.loopers.domain.user.dto;

import com.loopers.domain.user.BirthDate;
import com.loopers.domain.user.Email;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.LoginInfo;

public class UserCommand {
    public record Create(
        LoginInfo loginId,
        Email email,
        Gender gender,
        BirthDate birthDate
    ) {

    }
}
