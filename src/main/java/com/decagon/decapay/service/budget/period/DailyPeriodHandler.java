package com.decagon.decapay.service.budget.period;

import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.dto.CreateBudgetRequestDTO;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.utils.CustomDateUtil;

import java.time.LocalDate;

public class DailyPeriodHandler extends BudgetPeriodHandler {

    @Override
    public void validateRequest(CreateBudgetRequestDTO req) {

        LocalDate budgetStartDate =CustomDateUtil.formatStringToLocalDate(req.getBudgetStartDate(),DateDisplayConstants.DATE_INPUT_FORMAT) ;
        LocalDate budgetEndDate =CustomDateUtil.formatStringToLocalDate(req.getBudgetEndDate(),DateDisplayConstants.DATE_INPUT_FORMAT) ;

        if (budgetStartDate==null||budgetEndDate==null) {
            throw new InvalidRequestException("Budget start date and end date must be provided for DAILY period");
        }

        if (!budgetStartDate.equals(budgetEndDate)) {
            throw new InvalidRequestException("Budget start date must be equal to end date");
        }
    }

    @Override
    public LocalDate[] calculateBudgetDateRange(CreateBudgetRequestDTO dto) {
        return new LocalDate[]{CustomDateUtil.formatStringToLocalDate(dto.getBudgetStartDate(), DateDisplayConstants.DATE_INPUT_FORMAT),
                CustomDateUtil.formatStringToLocalDate(dto.getBudgetEndDate(),DateDisplayConstants.DATE_INPUT_FORMAT)};
    }

}
