package com.loopers.domain.user;

import java.util.Optional;

public interface UserRepository {
    boolean existByLoginInfo(LoginInfo loginInfo);
    UserModel save(UserModel user);
    Optional<UserModel> findByLoginInfo(LoginInfo loginInfo);
}
