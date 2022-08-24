package com.decagon.decapay.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BudgetExpensesResponseDto {
    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;


    public BudgetExpensesResponseDto(Long id, BigDecimal amount, String description, LocalDate transactionDate) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
    }
}
