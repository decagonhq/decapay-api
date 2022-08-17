package com.decagon.decapay.dto.budget;

import com.decagon.decapay.constants.DateDisplayConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private LocalDate displayEndDate;
    @JsonFormat(pattern = DateDisplayConstants.DATE_DISPLAY_FORMAT)
    private LocalDate displayStartDate;
    Collection<BudgetLineItem> lineItems = new ArrayList<>();

    @Setter
    @Getter
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class BudgetLineItem  {
        private long categoryId;
        private String category;
        private long budgetId;
        private BigDecimal projectedAmount;
        private BigDecimal totalAmountSpentSoFar;
        private BigDecimal percentageSpentSoFar;
        private String displayProjectedAmount;
        private String displayTotalAmountSpentSoFar;
        private String displayPercentageSpentSoFar;
    }
}
