package com.decagon.decapay.service.budget.period;

import com.decagon.decapay.dto.CreateBudgetRequestDTO;
import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.exception.InvalidRequestException;

import java.time.LocalDate;

public abstract class BudgetPeriodHandler {
    public static BudgetPeriodHandler getHandler(String period) {

        BudgetPeriod budgetPeriod = BudgetPeriod.valueOf(period);
        switch (budgetPeriod) {
            case CUSTOM -> {
                return new CustomPeriodHandler();
            }
            case DAILY -> {
                return new DailyPeriodHandler();
            }
            case ANNUAL -> {
                return new AnnualPeriodHandler();
            }
            case MONTHLY -> {
                return new MonthPeriodHandler();
            }
            case WEEKLY -> {
                return new WeeklyPeriodHandler();
            }
            default -> throw new InvalidRequestException();
        }
    }


    public abstract LocalDate[] calculateBudgetDateRange(CreateBudgetRequestDTO dto);

    public abstract void validateRequest(CreateBudgetRequestDTO req);


}
