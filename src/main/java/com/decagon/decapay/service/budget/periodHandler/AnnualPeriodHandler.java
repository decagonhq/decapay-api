package com.decagon.decapay.service.budget.periodHandler;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.utils.CustomDateUtil;

import java.time.LocalDate;

import static com.decagon.decapay.constants.ResponseMessageConstants.INVALID_REQUEST;

/**
 * handles Annual budgets
 */
public class AnnualPeriodHandler extends AbstractBudgetPeriodHandler {

    /**
     * validate Annual budget strategy.
     * Year is required and must be a number and 4 chatacters in length
     * @param req budget request input object to be validated based on selected period,also contains budget period
     */
    @Override
    public void validateRequest(CreateBudgetRequestDTO req) {
        if (req.getYear() < 1000 || req.getYear()>9999) {
            throw new InvalidRequestException(INVALID_REQUEST + ": Invalid value for Year (valid values 1000 - 9999): " + req.getYear());
        }
    }


    /**
     * Calculate annual budget period interval strategy
     * Given a year metadata, calculate the the start and end date for the budget.
     * Start date is first date of year and end date last date of year
     * @param dto budget request input object contains year
     * @return
     */
    @Override
    public LocalDate[] calculateBudgetPeriodInterval(CreateBudgetRequestDTO dto) {

        short requestBudgetYr = dto.getYear();
        if (requestBudgetYr < 1000 || requestBudgetYr>9999) {
            throw new InvalidRequestException(INVALID_REQUEST + ": Invalid value for Year (valid values 1000 - 9999): " + requestBudgetYr);
        }

        short currentYr = CustomDateUtil.getPresentYearValue();

        LocalDate startDate = null;
        LocalDate endDate = CustomDateUtil.lastDateOfYear(requestBudgetYr);

        //if  current year, then startdate is current date and enddate last date of year
        if (currentYr == requestBudgetYr) {//todo: remove redundant conditional statement
            //startDate = CustomDateUtil.today();
            startDate = CustomDateUtil.firstDateOfYear(requestBudgetYr);
        }else if(currentYr != requestBudgetYr){
            startDate = CustomDateUtil.firstDateOfYear(requestBudgetYr);
        }

        return new LocalDate[]{startDate, endDate};
    }


    /**
     * Set annual budget metadata strategy
     * populate Annual budget DTO object required year field with reverse engineered year
     *
     * @param dto    budget DTO to be populated
     * @param budget source budget, contains start and end date to reverse engineered
     */
    @Override
    public void setBudgetMetaData(CreateBudgetRequestDTO dto, Budget budget) {
        dto.setYear((short) budget.getBudgetStartDate().getYear());
    }
}
