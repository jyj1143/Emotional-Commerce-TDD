package com.loopers.domain.coupone.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class ExpirationTime {

    @Column(nullable = false)
    private LocalDateTime  expirationTime;

}
