package com.decagon.decapay.dto;


import com.decagon.decapay.model.budget.BudgetPeriod;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;


@Data
public class BudgetResponseDto {
    private Long id;
    private String title;
    private BigDecimal totalAmountSpentSoFar;
    private String displayTotalAmountSpentSoFar;
    private BigDecimal projectedAmount;
    private String displayProjectedAmount;
    @JsonProperty("period")
    private BudgetPeriod budgetPeriod;
    private Double percentageSpentSoFar;
    private String displayPercentageSpentSoFar;

    public BudgetResponseDto(Long id, String title, BigDecimal totalAmountSpentSoFar,  BigDecimal projectedAmount, BudgetPeriod budgetPeriod) {
        this.id = id;
        this.title = title;
        this.totalAmountSpentSoFar = totalAmountSpentSoFar;
        this.projectedAmount = projectedAmount;
        this.budgetPeriod = budgetPeriod;
    }
}
