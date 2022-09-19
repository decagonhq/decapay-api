package com.decagon.decapay.service.budget.periodHandler;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.constants.AppConstants;
import com.decagon.decapay.constants.DateConstants;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.utils.CustomDateUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * handles Weekly budgets
 */
public class WeeklyPeriodHandler extends AbstractBudgetPeriodHandler {

    /**
     * validate Weekly budget strategy.
     * Start date and duration required and duration must be a positive number
     * @param req budget request input object to be validated based on selected,also contains budget period
     */
    @Override
    public void validateRequest(CreateBudgetRequestDTO req) {
        if (StringUtils.isEmpty(req.getBudgetStartDate())) {
            throw new InvalidRequestException("Budget start date must be provided for WEEKLY period");
        }
        if (req.getDuration() <= 0) {
            throw new InvalidRequestException("Duration must be greater than 0 for WEEKLY period");
        }

        if (!CustomDateUtil.isValidFormat(DateConstants.DATE_INPUT_FORMAT, req.getBudgetStartDate())) {
            throw new InvalidRequestException("Budget start date not in valid format:" + DateConstants.DATE_INPUT_FORMAT);
        }

        LocalDate budgetStartDate = CustomDateUtil.formatStringToLocalDate(req.getBudgetStartDate(), DateConstants.DATE_INPUT_FORMAT);
        if (budgetStartDate == null) {
            throw new InvalidRequestException("Budget start date must be provided for WEEKLY period");
        }
    }


    @Override
    public LocalDate[] calculateBudgetPeriodInterval(CreateBudgetRequestDTO req) {
        LocalDate budgetStartDate = CustomDateUtil.formatStringToLocalDate(req.getBudgetStartDate(), DateConstants.DATE_INPUT_FORMAT);
        LocalDate budgetEndDate = budgetStartDate.plusDays(AppConstants.NUM_DAYS_IN_WEEK * req.getDuration());
        return new LocalDate[]{budgetStartDate, budgetEndDate};
    }

    /**
     * Set weekly budget metadata strategy
     * populate weekly budget DTO object required duration and start date metadata field with reverse engineered data
     *
     * @param dto    budget DTO to be populated
     * @param budget source budget, contains start and end date to reverse engineered
     */

    @Override
    public void setBudgetMetaData(CreateBudgetRequestDTO dto, Budget budget) {
        dto.setDuration((int) (DAYS.between(budget.getBudgetStartDate(), budget.getBudgetEndDate()) / AppConstants.NUM_DAYS_IN_WEEK));
        dto.setBudgetStartDate(CustomDateUtil.formatLocalDateToString(budget.getBudgetStartDate(), DateConstants.DATE_INPUT_FORMAT));
    }
}
