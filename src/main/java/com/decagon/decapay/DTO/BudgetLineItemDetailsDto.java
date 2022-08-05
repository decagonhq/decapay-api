package com.decagon.decapay.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class BudgetLineItemDetailsDto {
    private BigDecimal projectedAmount;
    private String notificationThreshold;
    private BudgetCategoryDto budgetCategory;
    private Collection<BudgetExpensesDto> expenses = new ArrayList<>();

}
