package com.decagon.decapay.service.budget;


import com.decagon.decapay.dto.common.SearchCriteria;
import com.decagon.decapay.dto.budget.*;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.service.budget.periodHandler.AbstractBudgetPeriodHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BudgetService {
	//CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest);
    CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest, AbstractBudgetPeriodHandler budgetPeriodHandler);

    Page<BudgetResponseDto> getBudgets(int pageSize, int pageNo, List<SearchCriteria> searchCriterias) throws JsonProcessingException;

    ViewBudgetDto viewBudgetDetails(Long budgetId) throws JsonProcessingException;
    IdResponseDto updateBudget(Long budgetId, CreateBudgetRequestDTO budgetRequestDto, AbstractBudgetPeriodHandler budgetPeriodHandler);

    CreateBudgetRequestDTO fetchBudget(Long budgetId);

    IdResponseDto createLineItem(Long budgetId, CreateBudgetLineItemDto budgetLineItemDto) throws JsonProcessingException;

    void updateLineItem(Long budgetId, Long categoryId, EditBudgetLineItemDto budgetLineItemDto) throws JsonProcessingException;

    void removeLineItem(Long budgetId, Long categoryId);

    IdResponseDto createExpense(Long budgetId, Long categoryId, ExpenseDto expenseDto);

    Page<BudgetExpensesResponseDto> getListOfBudgetExpenses(Long budgetId, Long categoryId, Pageable pageable) throws JsonProcessingException;

    void removeExpense(Long expenseId);

    IdResponseDto updateExpense(Long expenseId, ExpenseDto expenseDto);
}
