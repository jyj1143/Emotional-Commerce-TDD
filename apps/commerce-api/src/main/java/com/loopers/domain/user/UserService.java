package com.loopers.domain.user;

import com.loopers.domain.user.dto.UserCommand;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserModel signUp(UserCommand.Create command) {
        if (userRepository.existByLoginInfo(command.loginId())) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이미 존재하는 아이디입니다.");
        }
        UserModel user = UserModel.builder()
            .loginInfo(command.loginId())
            .email(command.email())
            .gender(command.gender())
            .birthDate(command.birthDate())
            .build();
        return userRepository.save(user);
    }

    public UserModel getUser(LoginInfo loginInfo) {
        return userRepository.findByLoginInfo(loginInfo).orElse(null);
    }

    @Transactional
    public void addPoint(LoginInfo loginInfo, Long point) {
        UserModel user = getUser(loginInfo);
        user.addPoint(point);
    }
}
