package com.decagon.decapay.config.userSetting;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;


@Data
@NoArgsConstructor
public class UserSettings  {
    private String countryCode;
    private String currencyCode;
    private String language;
    private Collection<UserBudgetLineItemTemplate> userBudgetLineItemTemplate;

    public void addUserBudgetPeriodCategorySetting(UserBudgetLineItemTemplate budgetLineItemTemplate){
      if(userBudgetLineItemTemplate==null){
          userBudgetLineItemTemplate=new HashSet<>();
      }
      userBudgetLineItemTemplate.add(budgetLineItemTemplate);
    }

}
