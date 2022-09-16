package com.decagon.decapay.config.userSetting;

import com.decagon.decapay.model.budget.BudgetPeriod;
import lombok.Data;

import java.util.Collection;
import java.util.HashSet;

/**
 * Class used to map a budget(based on period) to predefined budget line item for a user.
 *<p></p>
 * By default when a new budget is created,user need to add line items(budget category) to the budget
 * one after the other so user can log expense for each line item. This could be cumbersome and boring
 * giving the fact that some certain similar budgets(based on period) may have similar line item(s).
 *<p></p>
 * So when a user add a line item(category) to a budget, the user is presented a choice on the UI to set
 * the line item as a template and when set, the line item can be persisted for the budget type(based on period)
 * as a template for use later so user may not have to always add line items manually but just update the line item
 * amount when a new budget is created.<p></p>The system use this template automatically to create budget line item for the user
 * for budgets whose periods matches periods found in the preconfigured template.
 *
 */
@Data
public class UserBudgetLineItemTemplate {

    private BudgetPeriod period;
    private Collection<Long> budgetCategories;//line items
    public void addCategory(long category){
        if(budgetCategories==null){
           budgetCategories=new HashSet<>();
        }
        budgetCategories.add(category);
    }
}
