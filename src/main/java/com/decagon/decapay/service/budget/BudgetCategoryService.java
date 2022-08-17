package com.decagon.decapay.service.budget;

import com.decagon.decapay.dto.budget.BudgetCategoryResponseDto;
import com.decagon.decapay.dto.budget.CreateBudgetCategoryDto;
import com.decagon.decapay.dto.budget.CreateBudgetResponseDTO;

import java.util.List;

public interface BudgetCategoryService {

    List<BudgetCategoryResponseDto> getListOfBudgetCategories();

    CreateBudgetResponseDTO createBudgetCategory(CreateBudgetCategoryDto request);

}
