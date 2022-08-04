package com.decagon.decapay.service;

import org.springframework.stereotype.Service;

import com.decagon.decapay.dto.CreateBudgetRequestDTO;
import com.decagon.decapay.dto.CreateBudgetResponseDTO;

@Service
public interface BudgetService {
	CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest);
}
