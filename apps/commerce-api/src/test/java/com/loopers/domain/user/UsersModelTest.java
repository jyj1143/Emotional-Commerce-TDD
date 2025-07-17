package com.loopers.domain.user;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class UsersModelTest {

    @DisplayName("회원 모델을 생성할 때, ")
    @Nested
    class Create {

        @DisplayName("ID 가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
            "12345678901",
            "가나다라마바사아자차카",
            "@@@@@@@@@@@",
        })
        void givenInvalidLoginId_whenCreateUser_thenThrowException(String invalidLoginId) {
            // given
            // when
            // then
            assertThrows(CoreException.class, () -> {
                UserModel.builder()
                    .loginInfo(new LoginInfo(invalidLoginId))
                    .email(new Email("test@example.com"))
                    .gender(Gender.MALE)
                    .birthDate(new BirthDate("1997-02-27"))
                    .build()
                ;
            });
        }

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
            "12345678901",
            "가나다라마바사아자차카",
            "@@@@@@@@@@@",
        })
        void givenInvalidEmail_whenCreateUser_thenThrowException(String invalidEmail) {
            // given
            // when
            // then
            assertThrows(CoreException.class, () -> {
                UserModel.builder()
                    .loginInfo(new LoginInfo("test"))
                    .email(new Email(invalidEmail))
                    .gender(Gender.MALE)
                    .birthDate(new BirthDate("1997-02-27"))
                    .build()
                ;
            });
        }


        @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
            "12345678901",
            "가나다라마바사아자차카",
            "@@@@@@@@@@@",
        })
        void givenInvalidBirthDate_whenCreateUser_thenThrowException(String invalidBirthDate) {
            // given
            // when
            // then
            assertThrows(CoreException.class, () -> {
                UserModel.builder()
                    .loginInfo(new LoginInfo("test"))
                    .email(new Email("test@example.com"))
                    .gender(Gender.MALE)
                    .birthDate(new BirthDate(invalidBirthDate))
                    .build()
                ;
            });
        }
    }

    @DisplayName("포인 충전 시, ")
    @Nested
    class PointCharge {

        @DisplayName("포인트 충전 금액이 0 이하일 경우, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(longs = {0L, -100L})
        void givenInvalidPoint_whenChargePoint_thenFail(Long invalidPoint) {
            // given
            UserModel userModel = UserModel.builder()
                .loginInfo(new LoginInfo("test"))
                .email(new Email("test@example.com"))
                .gender(Gender.MALE)
                .birthDate(new BirthDate("1997-02-27"))
                .build();
            // when
            // then
            assertThrows(CoreException.class, () -> {
                userModel.addPoint(invalidPoint);
            });
        }
    }
}
