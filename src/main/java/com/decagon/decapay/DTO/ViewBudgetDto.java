package com.decagon.decapay.dto;

import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.dto.budget.BudgetLineItemDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Data
public class ViewBudgetDto {
    
    private Long id;

    private String title;

    @Digits(integer = 15,fraction =2)
    private BigDecimal projectedAmount;

    @Digits(integer = 15,fraction =2)
    private BigDecimal totalAmountSpentSoFar;

    @Digits(integer = 15,fraction =2)
    private BigDecimal percentageSpentSoFar;
    
    @JsonFormat(pattern = DateDisplayConstants.DATE_DB_FORMAT)
    @ApiModelProperty(notes = "Date format: " +DateDisplayConstants.DATE_DB_FORMAT)
    private LocalDateTime startDate;

    @JsonFormat(pattern = DateDisplayConstants.DATE_DB_FORMAT)
    @ApiModelProperty(notes = "Date format: " +DateDisplayConstants.DATE_DB_FORMAT)
    private LocalDateTime endDate;

 
    private String notificationThreshold;

    private String budgetPeriod;

    @ApiModelProperty(notes="Display projected budget amount")
    private String displayProjectedAmount;

    @ApiModelProperty(notes="Display total amount spent so far for budget")
    private String displayTotalAmountSpentSoFar;

    @ApiModelProperty(notes="Display calculated percentage of the amount spent so far for budget")
    private String displayPercentageSpentSoFar;

    @JsonFormat(pattern = DateDisplayConstants.DATE_DISPLAY_FORMAT)
    @ApiModelProperty(notes = "Display Date format: " +DateDisplayConstants.DATE_DISPLAY_FORMAT)
    private LocalDateTime displayEndDate;

    @JsonFormat(pattern = DateDisplayConstants.DATE_DISPLAY_FORMAT)
    @ApiModelProperty(notes = "Display Date format: " +DateDisplayConstants.DATE_DISPLAY_FORMAT)
    private LocalDateTime displayStartDate;

    @ApiModelProperty(notes="Display the budget line item if exist")
    Collection<BudgetLineItem> lineItems = new ArrayList<>();

}
