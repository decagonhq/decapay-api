package com.decagon.decapay.dto.budget;

import com.decagon.decapay.constants.DateConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BudgetExpensesResponseDto {
    private Long id;
    private BigDecimal amount;
    private String displayAmount;
    private String description;

    @JsonFormat(pattern = DateConstants.DATE_DB_FORMAT)
    private LocalDate transactionDate;

    @JsonFormat(pattern = DateConstants.DATE_DISPLAY_FORMAT)
    private LocalDate displayTransactionDate;



    public BudgetExpensesResponseDto(Long id, BigDecimal amount, String description, LocalDate transactionDate) {
        this.id = id;
        this.amount =amount;
        this.description = description;
        this.transactionDate = transactionDate;
        this.displayTransactionDate=transactionDate;
    }
}
