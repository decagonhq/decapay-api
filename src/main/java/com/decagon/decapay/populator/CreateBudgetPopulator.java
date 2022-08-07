package com.decagon.decapay.populator;

import com.decagon.decapay.dto.CreateBudgetRequestDTO;
import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.service.budget.period.BudgetPeriodHandler;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateBudgetPopulator extends
        AbstractDataPopulator<CreateBudgetRequestDTO, Budget> {
        BudgetPeriodHandler budgetPeriodHandler;
    @Override
    public Budget populate(CreateBudgetRequestDTO source, Budget target) {
        
        target.setTitle(source.getTitle());
        target.setProjectedAmount(source.getAmount());
        BudgetPeriod period = BudgetPeriod.valueOf(source.getPeriod());
        target.setBudgetPeriod(period);
        LocalDate[] targetdDateRange=budgetPeriodHandler.calculateBudgetDateRange(source);
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
