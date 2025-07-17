package com.loopers.domain.user;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.loopers.domain.user.dto.UserCommand;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private UserService userService;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("회원가입 시,")
    @Nested
    class Join {

        @DisplayName("User 저장이 수행된다. ( spy 검증 )")
        @Test
        void whenJoin_thenUserIsSaved() {
            //  given
            UserRepository spyUsersRepository = spy(userRepository);
            UserService spyUserService = new UserService(spyUsersRepository);
            UserCommand.Create create = new UserCommand.Create(
                new LoginInfo("test"),
                new Email("test@gmail.com"),
                Gender.MALE,
                new BirthDate("1997-02-27")
            );

            //  when
            spyUserService.signUp(create);

            //  then
            verify(spyUsersRepository, times(1)).existByLoginInfo(any(LoginInfo.class));
            verify(spyUsersRepository, times(1)).save(any(UserModel.class));
        }


        @DisplayName("이미 가입된 ID 로 회원가입 시도 시, 실패한다.")
        @Test
        void whenJoinWithExistingId_thenThrowsException() {
            // given
            LoginInfo loginInfo = new LoginInfo("test");
            // 첫 번째 회워가입 성공
            userService.signUp(new UserCommand.Create(
                loginInfo,
                new Email("test@gmail.com"),
                Gender.MALE,
                new BirthDate("1997-02-27")
            ));

            // when
            // then
            CoreException exception = assertThrows(CoreException.class, () ->
                userService.signUp(new UserCommand.Create(
                    loginInfo,
                    new Email("test2@gmail.com"),
                    Gender.MALE,
                    new BirthDate("2000-02-27")
                ))
            );
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("내 정보 조회 시,")
    @Nested
    class GetMyInfo {

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

            // then
            assertAll(
                () -> assertThat(userModel).isNotNull(),
                () -> assertThat(userModel.getLoginInfo().getLoginId()).isEqualTo(loginInfo.getLoginId()),
                () -> assertThat(userModel.getEmail().getEmail()).isEqualTo(email.getEmail()),
                () -> assertThat(userModel.getGender()).isEqualTo(male),
                () -> assertThat(userModel.getBirthDate().getBirthDate()).isEqualTo(birthDate.getBirthDate())
            );
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void whenNotExistUser_thenReturnUserInfo() {
            // given
            LoginInfo loginInfo = new LoginInfo("test");

            // when
            UserModel userModel = userService.getUser(loginInfo);

            // then
            assertAll(
                () -> assertThat(userModel).isNull()
            );
        }
    }
}
