package com.decagon.decapay.service.budget;


import com.decagon.decapay.dto.SearchCriteria;
import com.decagon.decapay.dto.budget.*;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.service.budget.periodHandler.AbstractBudgetPeriodHandler;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

public interface BudgetService {
	//CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest);
    CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest, AbstractBudgetPeriodHandler budgetPeriodHandler);

    Page<BudgetResponseDto> getBudgets(int pageSize, int pageNo, List<SearchCriteria> searchCriterias);

    ViewBudgetDto viewBudgetDetails(Long budgetId);
    IdResponseDto updateBudget(Long budgetId, CreateBudgetRequestDTO budgetRequestDto, AbstractBudgetPeriodHandler budgetPeriodHandler);

    CreateBudgetRequestDTO fetchBudget(Long budgetId);

    IdResponseDto createLineItem(Long budgetId, CreateBudgetLineItemDto budgetLineItemDto);

    void updateLineItem(Long budgetId, Long categoryId, EditBudgetLineItemDto budgetLineItemDto);

    void removeLineItem(Long budgetId, Long categoryId);

    Collection<BudgetExpensesResponseDto> getListOfBudgetExpenses(Long budgetId, Long categoryId);
}
