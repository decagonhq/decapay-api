package com.decagon.decapay.service;

import com.decagon.decapay.service.budget.period.BudgetPeriodHandler;
import org.springframework.stereotype.Service;

import com.decagon.decapay.dto.CreateBudgetRequestDTO;
import com.decagon.decapay.dto.CreateBudgetResponseDTO;

@Service
public interface BudgetService {
	//CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest);
    CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest, BudgetPeriodHandler budgetPeriodHandler);
}
