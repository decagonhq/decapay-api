package com.decagon.decapay.utils;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.model.auth.PasswordReset;
import com.decagon.decapay.model.budget.*;
import com.decagon.decapay.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    public static User aUSer() {
        return user("firstName","lastName", "a@b.com", "password", "0123456789");
    }

    public static PasswordReset passwordReset(String email, String token) {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setEmail(email);
        passwordReset.setToken(token);
        return passwordReset;
    }

    public static Budget budget(BudgetPeriod period, LocalDate start, LocalDate end) {
        Budget budget = new Budget();
        budget.setTitle("Budget");
        budget.setBudgetPeriod(period);
        budget.setBudgetStartDate(start);
        budget.setBudgetEndDate(end);
        return budget;
    }

    public static Expense expenses(BigDecimal amount, LocalDate transactionDate) {
        Expense expenses = new Expense();
        expenses.setAmount(amount);
        expenses.setTransactionDate(transactionDate);
        return expenses;
    }

    public static BudgetLineItem budgetLineItem(BigDecimal projectedAmount, BigDecimal amountSpent) {
        BudgetLineItem budgetLineItem = new BudgetLineItem();
        budgetLineItem.setProjectedAmount(projectedAmount);
        budgetLineItem.setTotalAmountSpentSoFar(amountSpent);
        return budgetLineItem;
    }

    public static BudgetCategory budgetCategory(String title) {
        BudgetCategory budgetCategory = new BudgetCategory();
        budgetCategory.setTitle(title);
        return budgetCategory;
    }


    public static UserSettings userSettings(String languageCode, String countryCode, String currencyCode){
        UserSettings userSettings = new UserSettings();
        userSettings.setLanguage(languageCode);
        userSettings.setCurrencyCode(currencyCode);
        userSettings.setCountryCode(countryCode);
        return userSettings;
    }

}
