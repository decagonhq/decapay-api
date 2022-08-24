package com.decagon.decapay.service.budget.expenses;

import com.decagon.decapay.dto.budget.BudgetExpensesResponseDto;

import java.util.Collection;

public interface ExpensesService {
    Collection<BudgetExpensesResponseDto> getListOfBudgetExpenses(Long budgetId, Long categoryId);
}
