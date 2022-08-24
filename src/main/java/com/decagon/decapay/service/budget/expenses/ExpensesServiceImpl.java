package com.decagon.decapay.service.budget.expenses;

import com.decagon.decapay.dto.budget.BudgetExpensesResponseDto;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.Expenses;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.budget.ExpenseRepository;
import com.decagon.decapay.service.budget.category.BudgetCategoryService;
import com.decagon.decapay.utils.UserInfoUtil;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpensesServiceImpl implements ExpensesService {
    private final BudgetRepository budgetRepository;
    private final BudgetCategoryService budgetCategoryService;
    private final ExpenseRepository expenseRepository;
    private final UserInfoUtil userInfoUtil;

    public ExpensesServiceImpl(BudgetRepository budgetRepository, BudgetCategoryService budgetCategoryService, ExpenseRepository expenseRepository, UserInfoUtil userInfoUtil) {
        this.budgetRepository = budgetRepository;
        this.budgetCategoryService = budgetCategoryService;
        this.expenseRepository = expenseRepository;
        this.userInfoUtil = userInfoUtil;
    }

    @Override
    public Collection<BudgetExpensesResponseDto> getListOfBudgetExpenses(Long budgetId, Long categoryId) {
        User currentUser = this.userInfoUtil.getCurrAuthUser();

        Optional<Budget> budget = this.budgetRepository.findBudgetWithLineItems(budgetId, currentUser.getId());
        if (budget.isEmpty()){
            return Collections.emptyList();
        }
        Optional<BudgetCategory> category = this.budgetCategoryService.findCategoryByIdAndUser(categoryId, currentUser);
        if (category.isEmpty()){
            return Collections.emptyList();
        }

        if (!isCurrentUserOwnerOfBudgetCategory(currentUser, category.get())) {
            return Collections.emptyList();
        }

        Collection<Expenses> expenses = expenseRepository.fetchExpensesDetails(budget.get().getId(), category.get().getId());
        if (expenses.isEmpty()){
            return Collections.emptyList();
        }

        return expenses.stream().map(exp -> {
            BudgetExpensesResponseDto dto = new BudgetExpensesResponseDto();
            dto.setId(exp.getId());
            dto.setAmount(exp.getAmount());
            dto.setTransactionDate(exp.getTransactionDate());
            dto.setDescription(dto.getDescription());
            return dto;
        }).collect(Collectors.toList());
    }

    private boolean isCurrentUserOwnerOfBudgetCategory(User user, BudgetCategory category) {
        return user.getId().equals(category.getUser().getId());
    }

}
