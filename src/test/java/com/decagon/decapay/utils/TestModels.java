package com.decagon.decapay.utils;

import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.BudgetLineItem;
import com.decagon.decapay.model.budget.Expenses;
import com.decagon.decapay.model.password.PasswordReset;
import com.decagon.decapay.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestModels {

    public static User user(String firstName, String lastName, String email, String password, String phoneNo) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber(phoneNo);
        return user;
    }

    public static PasswordReset passwordReset(String email, String token) {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setEmail(email);
        passwordReset.setToken(token);
        return passwordReset;
    }


    public static BudgetLineItem setUpBudgetLineItems(BudgetCategory budgetCategory){
        Expenses transportationExpense1 = new Expenses();
        transportationExpense1.setAmount(BigDecimal.valueOf(1300));
        transportationExpense1.setDescription("Day 1 Transportation");

        Expenses transportationExpense2 = new Expenses();
        transportationExpense2.setAmount(BigDecimal.valueOf(1200));
        transportationExpense2.setDescription("Day 2 Transportation");

        BudgetLineItem budgetLineItem = new BudgetLineItem();
        budgetLineItem.setNotificationThreshold("Notification ThreshHold");
        budgetLineItem.setBudgetCategory(budgetCategory);
        budgetLineItem.setProjectedAmount(BigDecimal.valueOf(5000));
        budgetLineItem.setTotalAmountSpentSoFar(BigDecimal.valueOf(2500));
        budgetLineItem.addExpense(transportationExpense1);
        budgetLineItem.addExpense(transportationExpense2);
        return budgetLineItem;
    }

    public static Budget setUpBudget(BudgetLineItem budgetLineItem){
        Budget budget = new Budget();
        LocalDateTime today = LocalDateTime.now();
        budget.setTitle("Transportation Budget");
        budget.setNotificationThreshold("Notification Trashold");
        budget.setBudgetPeriod(BudgetPeriod.MONTHLY);
        budget.setProjectedAmount(budgetLineItem.getProjectedAmount());
        budget.setBudgetStartDate(today);
        budget.setBudgetEndDate(today.plusWeeks(3));
        budget.addBudgetLineItem(budgetLineItem);
        return budget;
    }
}
