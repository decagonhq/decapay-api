package com.decagon.decapay.service.budget;

import com.decagon.decapay.dto.budget.BudgetCategoryResponseDto;

import java.util.List;

public interface BudgetCategoryService {

    List<BudgetCategoryResponseDto> getListOfBudgetCategories();
}
