package com.decagon.decapay.service.budget.periodHandler;

import com.decagon.decapay.DTO.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.utils.CustomDateUtil;

import java.time.LocalDate;

public class CustomPeriodHandler extends BudgetPeriodHandler {

    @Override
    public void validateRequest(CreateBudgetRequestDTO req) {

        if(!CustomDateUtil.isValidFormat(DateDisplayConstants.DATE_INPUT_FORMAT,req.getBudgetStartDate())
                ||!CustomDateUtil.isValidFormat(DateDisplayConstants.DATE_INPUT_FORMAT,req.getBudgetEndDate())){
            throw new InvalidRequestException("Budget start date or end date not in valid format:"+DateDisplayConstants.DATE_INPUT_FORMAT);
        }
        LocalDate budgetStartDate =CustomDateUtil.formatStringToLocalDate(req.getBudgetStartDate(),DateDisplayConstants.DATE_INPUT_FORMAT) ;
        LocalDate budgetEndDate =CustomDateUtil.formatStringToLocalDate(req.getBudgetEndDate(),DateDisplayConstants.DATE_INPUT_FORMAT) ;

        if (budgetStartDate==null||budgetEndDate==null) {
            throw new InvalidRequestException("Budget start date and end date must be provided for CUSTOM period");
        }
        if (!budgetStartDate.isBefore(budgetEndDate)) {
            throw new InvalidRequestException("Budget start date must be before end date");
        }
    }

    @Override
    public LocalDate[] calculateBudgetDateRange(CreateBudgetRequestDTO dto) {
        String budgetRequestStartDate= dto.getBudgetStartDate();
        String budgetRequestEndDate= dto.getBudgetEndDate();

        return new LocalDate[]{CustomDateUtil.formatStringToLocalDate(budgetRequestStartDate,DateDisplayConstants.DATE_INPUT_FORMAT),
                CustomDateUtil.formatStringToLocalDate(budgetRequestEndDate,DateDisplayConstants.DATE_INPUT_FORMAT)};
    }

}
