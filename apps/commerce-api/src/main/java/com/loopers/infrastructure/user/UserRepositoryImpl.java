package com.loopers.infrastructure.user;

import com.loopers.domain.user.LoginInfo;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean existByLoginInfo(LoginInfo loginInfo) {
        return userJpaRepository.existsByLoginInfo_LoginId(loginInfo.getLoginId());
    }

    @Override
    public UserModel save(UserModel user) {
        return userJpaRepository.save(user);
    }
}
