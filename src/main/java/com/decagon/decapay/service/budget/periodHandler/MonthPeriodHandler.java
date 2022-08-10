package com.decagon.decapay.service.budget.periodHandler;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.utils.CustomDateUtil;

import java.time.LocalDate;

import static com.decagon.decapay.constants.ResponseMessageConstants.INVALID_REQUEST;


public class MonthPeriodHandler extends AbstractBudgetPeriodHandler {
    /*
      for monthly period, year and month are both required
                         year must be a number and 4xters in len and
                         month must be number and btw 1 and 12
     */
    @Override
    public void validateRequest(CreateBudgetRequestDTO req) {
        if (req.getMonth() <= 0 || req.getMonth()>12) {
            throw new InvalidRequestException(INVALID_REQUEST + ": Invalid value for Month (valid values 1 - 12): " + req.getMonth());
        }
        if (req.getYear() < 1000 || req.getYear()>9999) {
            throw new InvalidRequestException(INVALID_REQUEST + ": Invalid value for Year (valid values 1000 - 9999): " + req.getYear());
        }
    }

    /**
     * if current month and year, then startdate is current date
     * if current month  and not current year, then start date is first date
     * if not current month but year current , start date is first date
     * if not current month and year ,start date is first date
     * enddate always last date of month
     *
     * @param dto
     * @return
     */
    public LocalDate[] calculateBudgetDateRange(CreateBudgetRequestDTO dto) {

        short requestBudgetMnth = dto.getMonth();
        short requestBudgetYr = dto.getYear();

        if (requestBudgetMnth <= 0 || requestBudgetMnth>12) {
            throw new InvalidRequestException(INVALID_REQUEST + ": Invalid value for Month (valid values 1 - 12): " + requestBudgetMnth);
        }

        short currentMnth = CustomDateUtil.getPresentMonthValue();
        short currentYr = CustomDateUtil.getPresentYearValue();

        LocalDate startDate = null;
        LocalDate endDate = CustomDateUtil.lastDateOfMonth(requestBudgetYr, requestBudgetMnth);

        //if not current month and year , then start date is current date and end date is last date of month
        if (currentMnth == requestBudgetMnth && currentYr == requestBudgetYr) {
            startDate = CustomDateUtil.today();
        } else {
            startDate = CustomDateUtil.firstDateOfMonth(requestBudgetYr, requestBudgetMnth);
        }
        return new LocalDate[]{startDate, endDate};
    }

}
