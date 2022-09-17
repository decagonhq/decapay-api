package com.decagon.decapay.service.budget.periodHandler;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.constants.DateConstants;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.utils.CustomDateUtil;

import java.time.LocalDate;

/**
 * handles Custom budgets
 */
public class CustomPeriodHandler extends AbstractBudgetPeriodHandler {

    /**
     * validate Custom budget strategy.
     * Start and end date are required and must be a valid date format
     * @param req budget request input object to be validated
     */
    @Override
    public void validateRequest(CreateBudgetRequestDTO req) {

        if(!CustomDateUtil.isValidFormat(DateConstants.DATE_INPUT_FORMAT,req.getBudgetStartDate())
                ||!CustomDateUtil.isValidFormat(DateConstants.DATE_INPUT_FORMAT,req.getBudgetEndDate())){
            throw new InvalidRequestException("Budget start date or end date not in valid format:"+ DateConstants.DATE_INPUT_FORMAT);
        }
        LocalDate budgetStartDate =CustomDateUtil.formatStringToLocalDate(req.getBudgetStartDate(), DateConstants.DATE_INPUT_FORMAT) ;
        LocalDate budgetEndDate =CustomDateUtil.formatStringToLocalDate(req.getBudgetEndDate(), DateConstants.DATE_INPUT_FORMAT) ;

        if (budgetStartDate==null||budgetEndDate==null) {
            throw new InvalidRequestException("Budget start date and end date must be provided for CUSTOM period");
        }
        if (!budgetStartDate.isBefore(budgetEndDate)) {
            throw new InvalidRequestException("Budget start date must be before end date");
        }
    }

    /**
     * Calculate custom budget period interval strategy
     * simply returns request start and end date
     * @param dto budget request input object contains start and end date
     * @return
     */
    @Override
    public LocalDate[] calculateBudgetDateRange(CreateBudgetRequestDTO dto) {
        String budgetRequestStartDate= dto.getBudgetStartDate();
        String budgetRequestEndDate= dto.getBudgetEndDate();

        return new LocalDate[]{CustomDateUtil.formatStringToLocalDate(budgetRequestStartDate, DateConstants.DATE_INPUT_FORMAT),
                CustomDateUtil.formatStringToLocalDate(budgetRequestEndDate, DateConstants.DATE_INPUT_FORMAT)};
    }

    /**
     * Set custom budget metadata strategy
     * Simply populate Custom budget DTO object required start and end date field with
     * budget start and end date. No reverse engineering required
     *
     * @param dto    budget DTO contains meta data fields to be populated
     * @param budget source budget, contains start and end date
     */

    @Override
    public void setBudgetPeriodMetaData(CreateBudgetRequestDTO dto, Budget budget) {
        dto.setBudgetStartDate(CustomDateUtil.formatLocalDateToString(budget.getBudgetStartDate(), DateConstants.DATE_INPUT_FORMAT));
        dto.setBudgetEndDate(CustomDateUtil.formatLocalDateToString(budget.getBudgetEndDate(), DateConstants.DATE_INPUT_FORMAT));
    }
}
