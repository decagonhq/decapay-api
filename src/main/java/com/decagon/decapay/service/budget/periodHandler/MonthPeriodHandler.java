package com.decagon.decapay.service.budget.periodHandler;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.utils.CustomDateUtil;

import java.time.LocalDate;

import static com.decagon.decapay.constants.ResponseMessageConstants.INVALID_REQUEST;

/**
 * handles Monthly budgets
 */
public class MonthPeriodHandler extends AbstractBudgetPeriodHandler {

    /**
     * validate Monthly budget strategy.
     * Year and month are required year must be a number
     * and 4 characters in length and month must be number and betweent 1 and 12
     * @param req budget request input object to be validated
     */
    @Override
    public void validateRequest(CreateBudgetRequestDTO req) {
        if (req.getMonth() <= 0 || req.getMonth() > 12) {
            throw new InvalidRequestException(INVALID_REQUEST + ": Invalid value for Month (valid values 1 - 12): " + req.getMonth());
        }
        if (req.getYear() < 1000 || req.getYear() > 9999) {
            throw new InvalidRequestException(INVALID_REQUEST + ": Invalid value for Year (valid values 1000 - 9999): " + req.getYear());
        }
    }

    /**
     * Calculate monthly budget period interval strategy
     * Given a year and month meta data, calculate the the start and end date for the budget
     * startdate is first date of month and enddate last date of month
     * @param dto budget request input object contains year and month metadata
     * @return
     */

    public LocalDate[] calculateBudgetDateRange(CreateBudgetRequestDTO dto) {

        short requestBudgetMnth = dto.getMonth();
        short requestBudgetYr = dto.getYear();

        if (requestBudgetMnth <= 0 || requestBudgetMnth > 12) {
            throw new InvalidRequestException(INVALID_REQUEST + ": Invalid value for Month (valid values 1 - 12): " + requestBudgetMnth);
        }

        short currentMnth = CustomDateUtil.getPresentMonthValue();
        short currentYr = CustomDateUtil.getPresentYearValue();

        LocalDate startDate = null;
        LocalDate endDate = CustomDateUtil.lastDateOfMonth(requestBudgetYr, requestBudgetMnth);

        //if not current month and year , then start date is current date and end date is last date of month//
        if (currentMnth == requestBudgetMnth && currentYr == requestBudgetYr) {//todo:remove redundant conditional check when confirm above commented requirement changes permanently
            //startDate = CustomDateUtil.today();
            startDate = CustomDateUtil.firstDateOfMonth(requestBudgetYr, requestBudgetMnth);
        } else {
            startDate = CustomDateUtil.firstDateOfMonth(requestBudgetYr, requestBudgetMnth);
        }
        return new LocalDate[]{startDate, endDate};
    }


    /**
     * Set monthly budget metadata strategy
     * populate Monthly budget DTO object required year and month field with reverse engineered data
     *
     * @param dto    budget DTO to be populated
     * @param budget source budget, contains start and end date to reverse engineered
     */
    @Override
    public void setBudgetPeriodMetaData(CreateBudgetRequestDTO dto, Budget budget) {
        dto.setMonth((short) budget.getBudgetStartDate().getMonthValue());
        dto.setYear((short) budget.getBudgetStartDate().getYear());
    }
}
