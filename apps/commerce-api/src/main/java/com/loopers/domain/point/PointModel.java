package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.point.vo.Amount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Version;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "point")
public class PointModel extends BaseEntity {

    @Column(name = "amount", nullable = false)
    private Amount amount;

    @Column(name = "ref_user_id", nullable = false)
    private Long refUserId;

    @Version
    Long version;

    private PointModel(Long amount, Long refUserId) {
        this.amount = new Amount(amount);
        this.refUserId = refUserId;
    }

    public static PointModel of(Long amount, Long refUserId) {
        return new PointModel(amount, refUserId);
    }


    public void charge(final Long amount) {
        this.amount.charge(amount);
    }

    public void use(final Long amount) {
        this.amount.use(amount);
    }
}
