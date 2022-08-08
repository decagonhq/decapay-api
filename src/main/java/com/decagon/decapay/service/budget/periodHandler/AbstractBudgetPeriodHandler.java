package com.decagon.decapay.service.budget.periodHandler;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.model.budget.BudgetPeriod;
import com.decagon.decapay.exception.InvalidRequestException;

import java.time.LocalDate;

public abstract class AbstractBudgetPeriodHandler {
    public static AbstractBudgetPeriodHandler getHandler(String period) {

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
