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

    @Embedded
    private Point point = new Point(0L); // 기본 포인트는 0으로 설정

    @Builder
    public UserModel(LoginInfo loginInfo, Gender gender, Email email, BirthDate birthDate, Point point) {
        this.loginInfo = loginInfo;
        this.email = email;
        this.gender = gender;
        this.birthDate = birthDate;
        this.point = point != null ? point : new Point(0L); // 기본 포인트는 0으로 설정
    }

    public void addPoint(Long point) {
        if (point == null || point < 0) {
            throw new IllegalArgumentException("포인트는 null이거나 음수일 수 없습니다.");
        }
        this.point = new Point(this.point.getPoint() + point);
    }

}
