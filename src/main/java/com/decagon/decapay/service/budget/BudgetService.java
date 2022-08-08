package com.decagon.decapay.service.budget;

import com.decagon.decapay.dto.*;
import com.decagon.decapay.dto.budget.BudgetResponseDto;
import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.dto.budget.CreateBudgetResponseDTO;
import com.decagon.decapay.dto.budget.ViewBudgetDto;
import com.decagon.decapay.service.budget.periodHandler.BudgetPeriodHandler;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BudgetService {
	//CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest);
    CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest, BudgetPeriodHandler budgetPeriodHandler);

    Page<BudgetResponseDto> getBudgets(int pageSize, int pageNo, List<SearchCriteria> searchCriterias);

    ViewBudgetDto viewBudgetDetails(Long budgetId);

}
