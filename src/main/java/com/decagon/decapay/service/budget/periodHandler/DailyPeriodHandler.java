package com.decagon.decapay.service.budget.periodHandler;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.constants.DateConstants;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.utils.CustomDateUtil;

import java.time.LocalDate;


/**
 * handles Daily budgets
 */
public class DailyPeriodHandler extends AbstractBudgetPeriodHandler {

    /**
     * validate Daily budget strategy.
     * Start date and end date required and must be a valid date format
     * @param req budget request input object to be validated
     */
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

    /**
     * Calculate daily budget period interval strategy
     * simply returns request start and end date
     * @param dto budget request input object contains start and end date
     * @return
     */
    @Override
    public LocalDate[] calculateBudgetPeriodInterval(CreateBudgetRequestDTO dto) {
        return new LocalDate[]{CustomDateUtil.formatStringToLocalDate(dto.getBudgetStartDate(), DateConstants.DATE_INPUT_FORMAT),
                CustomDateUtil.formatStringToLocalDate(dto.getBudgetEndDate(), DateConstants.DATE_INPUT_FORMAT)};
    }

    /**
     * Set daily budget metadata strategy
     * Simply populate Daily budget DTO object required start and end date field with
     * budget start and end date. No reverse engineering required
     *
     * @param dto    budget DTO contains meta data fields to be populated
     * @param budget source budget, contains start and end date
     */
    @Override
    public void setBudgetMetaData(CreateBudgetRequestDTO dto, Budget budget) {
        dto.setBudgetStartDate(CustomDateUtil.formatLocalDateToString(budget.getBudgetStartDate(), DateConstants.DATE_INPUT_FORMAT));
        dto.setBudgetEndDate(CustomDateUtil.formatLocalDateToString(budget.getBudgetEndDate(), DateConstants.DATE_INPUT_FORMAT));
    }
}
