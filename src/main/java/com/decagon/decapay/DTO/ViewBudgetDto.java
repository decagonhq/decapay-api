package com.decagon.decapay.dto;

import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.model.budget.BudgetLineItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @JsonFormat(pattern = DateDisplayConstants.DATE_DB_FORMAT)
    //private LocalDateTime startDate;
    private LocalDate startDate;
    @JsonFormat(pattern = DateDisplayConstants.DATE_DB_FORMAT)
    //private LocalDateTime endDate;
    private LocalDate endDate;
    private String notificationThreshold;
    private String budgetPeriod;
    private String displayProjectedAmount;
    private String displayTotalAmountSpentSoFar;
    private String displayPercentageSpentSoFar;
    @JsonFormat(pattern = DateDisplayConstants.DATE_DISPLAY_FORMAT)
   // private LocalDateTime displayEndDate;
    private LocalDate displayEndDate;
    @JsonFormat(pattern = DateDisplayConstants.DATE_DISPLAY_FORMAT)
    //private LocalDateTime displayStartDate;
    private LocalDate displayStartDate;
    Collection<BudgetLineItemDto> lineItems = new ArrayList<>();
}
