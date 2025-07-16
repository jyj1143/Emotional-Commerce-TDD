package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class BirthDate {

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    public BirthDate(String birthDate) {
        if (birthDate == null || birthDate.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST,"Birth date cannot be null or empty.");
        }
        if (!birthDate.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Invalid date format. Please use 'YYYY-MM-DD'.");
        }
        try {
            this.birthDate = LocalDate.parse(birthDate);
        } catch (Exception e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Invalid date format. Please use 'YYYY-MM-DD'.");
        }
    }

}
