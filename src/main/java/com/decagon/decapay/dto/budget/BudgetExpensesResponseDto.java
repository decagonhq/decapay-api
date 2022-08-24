package com.decagon.decapay.dto.budget;

import com.decagon.decapay.constants.AppConstants;
import com.decagon.decapay.constants.DateDisplayConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Data
@NoArgsConstructor
public class BudgetExpensesResponseDto {
    private Long id;
    private BigDecimal amount;
    private String displayAmount;
    private String description;
    private LocalDate transactionDate;

    @JsonFormat(pattern = DateDisplayConstants.DATE_DISPLAY_FORMAT)
    private LocalDate displayTransactionDate;



    public BudgetExpensesResponseDto(Long id, BigDecimal amount, String description, LocalDate transactionDate) {
        this.id = id;
        this.amount =amount;
        this.description = description;
        this.transactionDate = transactionDate;
        this.displayTransactionDate=transactionDate;
    }
}
