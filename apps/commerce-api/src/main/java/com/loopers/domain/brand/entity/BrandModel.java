package com.loopers.domain.brand.entity;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.brand.vo.BrandName;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "brand")
public class BrandModel extends BaseEntity {

    @Embedded
    private BrandName name;

    private BrandModel(String name) {
        this.name = BrandName.of(name);
    }

    public static BrandModel of(String name) {
        return new BrandModel(name);
    }

}
