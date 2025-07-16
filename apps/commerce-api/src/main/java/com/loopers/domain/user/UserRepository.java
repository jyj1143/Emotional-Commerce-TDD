package com.loopers.domain.user;

public interface UserRepository {
    boolean existByLoginInfo(LoginInfo loginInfo);
    UserModel save(UserModel user);
}
