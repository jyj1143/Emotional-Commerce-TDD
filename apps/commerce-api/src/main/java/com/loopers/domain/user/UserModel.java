package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member")
public class UserModel extends BaseEntity {

    @Embedded
    private LoginInfo loginInfo;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Email email;

    @Embedded
    private BirthDate birthDate;

    @Builder
    public UserModel(LoginInfo loginInfo, Gender gender, Email email, BirthDate birthDate){
        this.loginInfo = loginInfo;
        this.email = email;
        this.gender = gender;
        this.birthDate = birthDate;
    }

}
