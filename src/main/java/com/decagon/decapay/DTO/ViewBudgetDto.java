package com.decagon.decapay.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Data
public class ViewBudgetDto {
    private Long id;
    private String title;
    private BigDecimal projectedAmount;
    private BigDecimal totalAmountSpentSoFar;
    private BigDecimal percentageSpentSoFar;
    private LocalDateTime budgetStartDate;
    private LocalDateTime budgetEndDate;
    private String notificationThreshold;
    private String budgetPeriod;
    private Collection<BudgetLineItemDetailsDto> lineItems = new ArrayList<>();


}
