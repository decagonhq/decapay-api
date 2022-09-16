package com.decagon.decapay.service.budget.periodHandler;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.constants.DateConstants;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.utils.CustomDateUtil;

import java.time.LocalDate;

public class DailyPeriodHandler extends AbstractBudgetPeriodHandler {

    @Override
    public void validateRequest(CreateBudgetRequestDTO req) {

        LocalDate budgetStartDate =CustomDateUtil.formatStringToLocalDate(req.getBudgetStartDate(), DateConstants.DATE_INPUT_FORMAT) ;
        LocalDate budgetEndDate =CustomDateUtil.formatStringToLocalDate(req.getBudgetEndDate(), DateConstants.DATE_INPUT_FORMAT) ;

        if (budgetStartDate==null||budgetEndDate==null) {
            throw new InvalidRequestException("Budget start date and end date must be provided for DAILY period");
        }

        if (!budgetStartDate.equals(budgetEndDate)) {
            throw new InvalidRequestException("Budget start date must be equal to end date");
        }
    }

    @Override
    public LocalDate[] calculateBudgetDateRange(CreateBudgetRequestDTO dto) {
        return new LocalDate[]{CustomDateUtil.formatStringToLocalDate(dto.getBudgetStartDate(), DateConstants.DATE_INPUT_FORMAT),
                CustomDateUtil.formatStringToLocalDate(dto.getBudgetEndDate(), DateConstants.DATE_INPUT_FORMAT)};
    }

    @Override
    public void setBudgetPeriodMetaData(CreateBudgetRequestDTO dto, Budget budget) {
        dto.setBudgetStartDate(CustomDateUtil.formatLocalDateToString(budget.getBudgetStartDate(), DateConstants.DATE_INPUT_FORMAT));
        dto.setBudgetEndDate(CustomDateUtil.formatLocalDateToString(budget.getBudgetEndDate(), DateConstants.DATE_INPUT_FORMAT));
    }
}
