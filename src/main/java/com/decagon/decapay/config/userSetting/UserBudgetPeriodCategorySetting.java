package com.decagon.decapay.config.userSetting;

import com.decagon.decapay.model.budget.BudgetPeriod;
import lombok.Data;
import net.minidev.json.JSONAware;

import java.util.Collection;

@Data
public class UserBudgetPeriodCategorySetting {
    private BudgetPeriod period;
    private Collection<Long> budgetCategories;
}
