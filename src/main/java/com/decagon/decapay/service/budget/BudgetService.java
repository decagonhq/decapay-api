package com.decagon.decapay.service.budget;


import com.decagon.decapay.DTO.SearchCriteria;
import com.decagon.decapay.DTO.budget.BudgetResponseDto;
import com.decagon.decapay.DTO.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.DTO.budget.CreateBudgetResponseDTO;
import com.decagon.decapay.DTO.budget.ViewBudgetDto;
import com.decagon.decapay.DTO.common.IdResponseDto;
import com.decagon.decapay.payloads.request.budget.UpdateBudgetRequestDto;
import com.decagon.decapay.service.budget.periodHandler.BudgetPeriodHandler;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BudgetService {
	//CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest);
    CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest, BudgetPeriodHandler budgetPeriodHandler);

    Page<BudgetResponseDto> getBudgets(int pageSize, int pageNo, List<SearchCriteria> searchCriterias);

    ViewBudgetDto viewBudgetDetails(Long budgetId);
    IdResponseDto updateBudget(Long budgetId, UpdateBudgetRequestDto budgetRequestDto, BudgetPeriodHandler budgetPeriodHandler);

}
