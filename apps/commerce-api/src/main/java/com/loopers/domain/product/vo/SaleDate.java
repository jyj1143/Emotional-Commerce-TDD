package com.loopers.domain.product.vo;

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
public class SaleDate {

    @Column(name = "sale_date", nullable = true)
    private LocalDate saleDate;

    private SaleDate(String saleDate) {
        if (saleDate == null) {
            this.saleDate = null;
            return;
        }

        if (!saleDate.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Invalid date format. Please use 'YYYY-MM-DD'.");
        }
        try {
            this.saleDate = LocalDate.parse(saleDate);
        } catch (Exception e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "Invalid date format. Please use 'YYYY-MM-DD'.");
        }
    }

    public static SaleDate of(String saleDate) {
        return new SaleDate(saleDate);
    }
}
