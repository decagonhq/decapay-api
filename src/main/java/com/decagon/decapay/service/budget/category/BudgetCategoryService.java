package com.decagon.decapay.service.budget.category;

import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.user.User;

import java.util.Optional;

public interface BudgetCategoryService {
    Optional<BudgetCategory> findCategoryByIdAndUser(Long budgetCategoryId, User userId);
}
