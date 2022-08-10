package com.decagon.decapay.service.budget.periodHandler;

import com.decagon.decapay.DTO.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.utils.CustomDateUtil;

import java.time.LocalDate;

import static com.decagon.decapay.constants.ResponseMessageConstants.INVALID_REQUEST;

public class AnnualPeriodHandler extends BudgetPeriodHandler {

    /**
     * if current year, then startdate is current date and enddate last date of year
     * if not current year, then startdate is first date of year and enddate last date of year
     * @param dto
     * @return
     */
    @Override
    public LocalDate[] calculateBudgetDateRange(CreateBudgetRequestDTO dto) {

        short requestBudgetYr = dto.getYear();
        if (requestBudgetYr < 1000 || requestBudgetYr>9999) {
            throw new InvalidRequestException(INVALID_REQUEST + ": Invalid value for Year (valid values 1000 - 9999): " + requestBudgetYr);
        }

        short currentYr = CustomDateUtil.getPresentYearValue();

        LocalDate startDate = null;
        LocalDate endDate = CustomDateUtil.lastDateOfYear(requestBudgetYr);

        //if  current year, then startdate is current date and enddate last date of year
        if (currentYr == requestBudgetYr) {
            startDate = CustomDateUtil.today();
        }else if(currentYr != requestBudgetYr){
            startDate = CustomDateUtil.firstDateOfYear(requestBudgetYr);
        }

        return new LocalDate[]{startDate, endDate};
    }

    @Override
    public void validateRequest(CreateBudgetRequestDTO req) {
        if (req.getYear() < 1000 || req.getYear()>9999) {
            throw new InvalidRequestException(INVALID_REQUEST + ": Invalid value for Year (valid values 1000 - 9999): " + req.getYear());
        }
    }
}