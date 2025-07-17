package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.domain.user.BirthDate;
import com.loopers.domain.user.Email;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.LoginInfo;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserRepository;
import com.loopers.domain.user.UserService;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PointServiceIntegrationTest {

    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private UserService userService;


    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("포인트 조회 시,")
    @Nested
    class GetPoint {

        @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        void whenExistUser_thenReturnUserInfo() {
            // given
            LoginInfo loginInfo = new LoginInfo("test");
            Email email = new Email("test@gmail.com");
            Gender male = Gender.MALE;
            BirthDate birthDate = new BirthDate("1997-02-27");
            UserCommand.Create user = new UserCommand.Create(
                loginInfo,
                email,
                male,
                birthDate
            );
            userService.signUp(user);

            // when
            UserModel userModel = userService.getUser(loginInfo);
            userModel.addPoint(1000L);
            // then
            assertAll(
                () -> assertThat(userModel).isNotNull(),
                () -> assertThat(userModel.getLoginInfo().getLoginId()).isEqualTo(loginInfo.getLoginId()),
                () -> assertThat(userModel.getEmail().getEmail()).isEqualTo(email.getEmail()),
                () -> assertThat(userModel.getGender()).isEqualTo(male),
                () -> assertThat(userModel.getBirthDate().getBirthDate()).isEqualTo(birthDate.getBirthDate()),
                () -> assertThat(userModel.getPoint().getPoint()).isEqualTo(1000L)
            );
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void whenNotExistUser_whenRequestPoint_thenReturnNull() {
            // given
            LoginInfo loginInfo = new LoginInfo("test");

            // when
            UserModel user = userService.getUser(loginInfo);

            // then
            assertAll(
                () -> assertThat(user).isNull()
            );
        }
    }
}
