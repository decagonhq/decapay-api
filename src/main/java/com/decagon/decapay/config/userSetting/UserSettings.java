package com.decagon.decapay.config.userSetting;

import com.decagon.decapay.model.budget.BudgetPeriod;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;


@Data
@NoArgsConstructor
public class UserSettings  {
    private String countryCode;
    private String currencyCode;
    private String language;
    private Collection<UserBudgetLineItemTemplate> userBudgetLineItemTemplate;

    public void addBudgetLineItemTemplateSetting(UserBudgetLineItemTemplate budgetLineItemTemplate){
      if(userBudgetLineItemTemplate==null){
          userBudgetLineItemTemplate=new HashSet<>();
      }
      userBudgetLineItemTemplate.add(budgetLineItemTemplate);
    }

    public Optional<UserBudgetLineItemTemplate> getBudgetLineItemTemplateByPeriod(BudgetPeriod budgetPeriod) {
        if(this.getUserBudgetLineItemTemplate()==null){
          return Optional.empty();
        }
        return this.getUserBudgetLineItemTemplate().stream().filter(period -> period.getPeriod().equals(budgetPeriod)).findFirst();
    }
}
