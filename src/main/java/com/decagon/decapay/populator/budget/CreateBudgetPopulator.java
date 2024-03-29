package com.decagon.decapay.populator.budget;

import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.model.budget.BudgetPeriod;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.populator.AbstractDataPopulator;
import com.decagon.decapay.service.budget.periodHandler.AbstractBudgetPeriodHandler;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateBudgetPopulator extends
        AbstractDataPopulator<CreateBudgetRequestDTO, Budget> {
    AbstractBudgetPeriodHandler budgetPeriodHandler;

    @Override
    public Budget populate(CreateBudgetRequestDTO source, Budget target) {

        target.setTitle(source.getTitle());
        target.setProjectedAmount(source.getAmount());
        BudgetPeriod period = BudgetPeriod.valueOf(source.getPeriod());
        target.setBudgetPeriod(period);
        LocalDate[] targetdDateRange = budgetPeriodHandler.calculateBudgetPeriodInterval(source);
        target.setBudgetStartDate(targetdDateRange[0]);
        target.setBudgetEndDate(targetdDateRange[1]);
        target.setDescription(source.getDescription());
        return target;
    }


    @Override
    protected Budget createTarget() {
        return null;
    }

}
