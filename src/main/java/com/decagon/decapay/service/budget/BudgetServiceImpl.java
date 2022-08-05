package com.decagon.decapay.service.budget;


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
import com.decagon.decapay.utils.UserInfoUtills;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BudgetServiceImpl implements BudgetService{

    private final UserInfoUtills userInfoUtills;

    private final UserRepository userRepository;

    private final BudgetRepository budgetRepository;

    @Override
    public ViewBudgetDto viewBudgetDetails(Long budgetId) {
        User user = this.getAuthenticatedUser();

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(()-> new ResourceNotFoundException("Resource Not Found"));

        if (!user.equals(budget.getUser())){
            throw new InvalidRequestException("Invalid Request");
        }

        return this.convertBudgetViewDto(budget);
    }

    private ViewBudgetDto convertBudgetViewDto(Budget budget) {
                ViewBudgetDto budgetViewDto = new ViewBudgetDto();
                budgetViewDto.setId(budget.getId());
                budgetViewDto.setTitle(budget.getTitle());
                budgetViewDto.setBudgetPeriod(budget.getBudgetPeriod().name());
                budgetViewDto.setBudgetStartDate(budget.getBudgetStartDate());
                budgetViewDto.setBudgetEndDate(budget.getBudgetEndDate());
                budgetViewDto.setNotificationThreshold(budget.getNotificationThreshold());
                budgetViewDto.setProjectedAmount(budget.getProjectedAmount());

                final BigDecimal[] totalExpenses = {BigDecimal.ZERO};

                Collection<BudgetLineItem> budgetLineItems= budget.getBudgetLineItems();

                if (!Collections.isEmpty(budgetLineItems)){
                    var lineItems = budgetLineItems.stream().map(budgetLineItem -> {
                        BudgetLineItemDetailsDto budgetDto = new BudgetLineItemDetailsDto();
                        this.populateLineItems(budgetLineItem, budgetDto);

                        budgetDto.setExpenses(
                                budgetLineItem.getExpenses().stream().map(expenses -> {totalExpenses[0] = totalExpenses[0].add(expenses.getAmount());
                                    return new BudgetExpensesDto(expenses.getId(), expenses.getAmount(), expenses.getDescription());}).collect(Collectors.toList())
                        );
                        return budgetDto;
                    }).collect(Collectors.toList());

                    budgetViewDto.setLineItems(lineItems);
                    BigDecimal spentSoFar =(totalExpenses[0].divide(budget.getProjectedAmount()));
                    BigDecimal percentageSpentSoFar = spentSoFar.multiply(BigDecimal.valueOf(100));
                    budgetViewDto.setPercentageSpentSoFar(percentageSpentSoFar);
                }

                budgetViewDto.setTotalAmountSpentSoFar(totalExpenses[0]);

                return budgetViewDto;
    }

    private void populateLineItems(BudgetLineItem budgetLineItem, BudgetLineItemDetailsDto budgetDto) {
        budgetDto.setBudgetCategory(new BudgetCategoryDto(budgetLineItem.getBudgetCategory().getId(), budgetLineItem.getBudgetCategory().getTitle()));
        budgetDto.setProjectedAmount(budgetLineItem.getProjectedAmount());
        budgetDto.setNotificationThreshold(budgetLineItem.getNotificationThreshold());
    }


    public User getAuthenticatedUser() {
        UserInfo authenticatedUserInfo = this.userInfoUtills.authenticationUserInfo();
        if (authenticatedUserInfo == null){
            throw new UnAuthorizedException("Authenticated User not found");
        }
        return this.userRepository.findUserByEmail(authenticatedUserInfo.getUsername()).get();
    }
}
