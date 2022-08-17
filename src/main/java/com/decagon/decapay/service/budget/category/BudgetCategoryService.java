package com.decagon.decapay.service.budget.category;

import com.decagon.decapay.model.budget.BudgetCategory;

import java.util.Optional;

public interface BudgetCategoryService {
    Optional<BudgetCategory> findCategoryById(Long budgetCategoryId);
}
