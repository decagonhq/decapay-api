package com.decagon.decapay.service.budget;


import com.decagon.decapay.constants.AppConstants;
import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.dto.BudgetCategoryDto;
import com.decagon.decapay.dto.BudgetExpensesDto;
import com.decagon.decapay.dto.BudgetLineItemDetailsDto;
import com.decagon.decapay.dto.ViewBudgetDto;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.exception.UnAuthorizedException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetLineItem;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.UserInfo;
import com.decagon.decapay.service.currency.CurrencyService;
import com.decagon.decapay.utils.CustomDateUtil;
import com.decagon.decapay.utils.UserInfoUtills;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BudgetServiceImpl implements BudgetService{
    private final UserInfoUtills userInfoUtills;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final CurrencyService currencyService;

    @Override
    public ViewBudgetDto viewBudgetDetails(Long budgetId) {
        User user = this.getAuthenticatedUser();

        Optional<Budget> optionalBudget = budgetRepository.findBudgetDetailsById(budgetId);

        if (optionalBudget.isEmpty()){
            throw new ResourceNotFoundException("Resource Not Found");
        }

        Budget budget = optionalBudget.get();
        if (!user.getId().equals(budget.getUser().getId())){
            throw new InvalidRequestException("Invalid Request");
        }

        return this.convertBudgetViewDto(budget);
    }

    private ViewBudgetDto convertBudgetViewDto(Budget budget) {
        ViewBudgetDto budgetViewDto = new ViewBudgetDto();
        budgetViewDto.setId(budget.getId());
        budgetViewDto.setTitle(budget.getTitle());
        budgetViewDto.setBudgetPeriod(budget.getBudgetPeriod().name());
        budgetViewDto.setNotificationThreshold(budget.getNotificationThreshold());
        budgetViewDto.setProjectedAmount(budget.getProjectedAmount());
        budgetViewDto.setTotalAmountSpentSoFar(budget.getTotalAmountSpentSoFar());
        budgetViewDto.setEndDate(budget.getBudgetEndDate());
        budgetViewDto.setStartDate(budget.getBudgetStartDate());
        budgetViewDto.setDisplayEndDate(budget.getBudgetEndDate());
        budgetViewDto.setDisplayStartDate(budget.getBudgetStartDate());
        budgetViewDto.setDisplayProjectedAmount(currencyService.formatAmount(budget.getProjectedAmount()));
        budgetViewDto.setDisplayTotalAmountSpentSoFar(currencyService.formatAmount(budget.getTotalAmountSpentSoFar()));
        BigDecimal percentageSpentSoFar = budget.calculatePercentageAmountSpent();
        budgetViewDto.setPercentageSpentSoFar(percentageSpentSoFar);
        budgetViewDto.setDisplayPercentageSpentSoFar(percentageSpentSoFar+"%");
        return budgetViewDto;
    }
    public User getAuthenticatedUser() {
        UserInfo authenticatedUserInfo = this.userInfoUtills.authenticationUserInfo();
        if (authenticatedUserInfo == null){
            throw new UnAuthorizedException("Authenticated User not found");
        }
        return this.userRepository.findUserByEmail(authenticatedUserInfo.getUsername()).get();
    }
}
