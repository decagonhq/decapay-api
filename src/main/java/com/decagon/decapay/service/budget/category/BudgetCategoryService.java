package com.decagon.decapay.service.budget.category;

import com.decagon.decapay.dto.budget.BudgetCategoryResponseDto;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.user.User;

import java.util.List;
import java.util.Optional;

public interface BudgetCategoryService {
    Optional<BudgetCategory> findCategoryByIdAndUser(Long budgetCategoryId, User userId);

    List<BudgetCategoryResponseDto> getListOfBudgetCategories();

}
