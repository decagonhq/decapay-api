package com.decagon.decapay.service.budget.periodHandler;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetPeriod;
import com.decagon.decapay.exception.InvalidRequestException;

import java.time.LocalDate;

/***
 * Class contains abstract methods to handles various strategies to process different budgets
 * types based on the period.
 * <p></p>
 */
public abstract class AbstractBudgetPeriodHandler {
    public static AbstractBudgetPeriodHandler getHandler(String period) {

        BudgetPeriod budgetPeriod = BudgetPeriod.valueOf(period);
        switch (budgetPeriod) {
            case CUSTOM -> {
                return new CustomPeriodHandler();
            }
            case DAILY -> {
                return new DailyPeriodHandler();
            }
            case ANNUAL -> {
                return new AnnualPeriodHandler();
            }
            case MONTHLY -> {
                return new MonthPeriodHandler();
            }
            case WEEKLY -> {
                return new WeeklyPeriodHandler();
            }
            default -> throw new InvalidRequestException();
        }
    }

    /**
     * validate budget request strategy based on budget's request period.
     * Each budget has different input meta data used to calculate the budget's period interval
     *
     * @param req input request object to be used to create a new budget, contains budget period
     * @see MonthPeriodHandler#validateRequest(CreateBudgetRequestDTO)
     * @see AnnualPeriodHandler#validateRequest(CreateBudgetRequestDTO)
     * @see WeeklyPeriodHandler#validateRequest(CreateBudgetRequestDTO)
     * @see DailyPeriodHandler#validateRequest(CreateBudgetRequestDTO)
     * @see CustomPeriodHandler#validateRequest(CreateBudgetRequestDTO)
     */
    public abstract void validateRequest(CreateBudgetRequestDTO req);

    /**
     * Calculate budget period interval strategy based on supplied meta data
     * When a budget is created, start and end date need to be calculated based on period using different
     * input metadata, except for CUSTOM and DAILY period whose start and end date are set by the users.
     * <p></p>
     * This method calculate budget's start and end date based on the meta data. CUSTOM and DAILY
     * budget request simply returns date selected
     * <p></p>
     *
     * @param req input request object to be used to create a new budget, contains budget meta data
     * @return array of LocalDates contain calculate start and end date
     * @see MonthPeriodHandler#calculateBudgetDateRange(CreateBudgetRequestDTO)
     * @see AnnualPeriodHandler#calculateBudgetDateRange(CreateBudgetRequestDTO)
     * @see WeeklyPeriodHandler#calculateBudgetDateRange(CreateBudgetRequestDTO)
     * @see DailyPeriodHandler#calculateBudgetDateRange(CreateBudgetRequestDTO)
     * @see CustomPeriodHandler#calculateBudgetDateRange(CreateBudgetRequestDTO)
     */
    public abstract LocalDate[] calculateBudgetDateRange(CreateBudgetRequestDTO req);


    /**
     * Set budget meta data strategy based on period
     * populate budget input object with specfic meta data {@link #calculateBudgetDateRange(CreateBudgetRequestDTO)}
     * These meta data are reverse engineered from the budget start and end date.
     *
     * @param dto    budget input to be populated
     * @param budget source budget, contains start and end date to reverse engineered
     * @see MonthPeriodHandler#setBudgetPeriodMetaData(CreateBudgetRequestDTO, Budget)
     * @see AnnualPeriodHandler#setBudgetPeriodMetaData(CreateBudgetRequestDTO, Budget)
     * @see WeeklyPeriodHandler#setBudgetPeriodMetaData(CreateBudgetRequestDTO, Budget)
     * @see DailyPeriodHandler#setBudgetPeriodMetaData(CreateBudgetRequestDTO, Budget)
     * @see CustomPeriodHandler#setBudgetPeriodMetaData(CreateBudgetRequestDTO, Budget)
     */
    public abstract void setBudgetPeriodMetaData(CreateBudgetRequestDTO dto, Budget budget);


}
