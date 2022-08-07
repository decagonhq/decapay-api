package com.decagon.decapay.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.decagon.decapay.dto.CreateBudgetRequestDTO;
import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.utils.TestModels;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

	@Mock
	private CustomUserDetailsService userDetailsService;

	@Mock
	private BudgetRepository budgetRepository;

	@InjectMocks
	private BudgetServiceImpl budgetService;

	private CreateBudgetRequestDTO budgetRequest;

/*	@Test
	void createBudget() {
		//budgetRequest =
			//new CreateBudgetRequestDTO("Title", BigDecimal.TEN, "CUSTOM", LocalDate.of(2022, 01, 01),
				//LocalDate.of(2022, 01, 02), "des");
		Budget budget = budgetFromCreateBudgetRequestDTO(budgetRequest);

		Budget savedCustomBudget = budgetFromCreateBudgetRequestDTO(budgetRequest);
		savedCustomBudget.setId(1L);

		when(userDetailsService.getLoggedInUser()).thenReturn(TestModels.aUSer());
		when(budgetRepository.save(budget)).thenReturn(savedCustomBudget);

		budgetService.createBudget(budgetRequest);
		verify(budgetRepository, times(1)).save(budget);

		budgetRequest.setBudgetEndDate(budgetRequest.getBudgetStartDate());
		assertThrows(InvalidRequestException.class, () -> budgetService.createBudget(budgetRequest),
			"Budget start date must be before end date");

		budgetRequest.setBudgetStartDate(null);
		assertThrows(InvalidRequestException.class, () -> budgetService.createBudget(budgetRequest),
			"Budget start date and end date must be provided for CUSTOM period");
	}

	private Budget budgetFromCreateBudgetRequestDTO(CreateBudgetRequestDTO budgetRequest) {
		Budget budget1 = new Budget();

		budget1.setTitle(budgetRequest.getTitle());
		budget1.setProjectedAmount(budgetRequest.getAmount());

		BudgetPeriod period = BudgetPeriod.valueOf(budgetRequest.getPeriod());

		//budget1.setBudgetStartDate(budgetRequest.getBudgetStartDate());
		//budget1.setBudgetEndDate(budgetRequest.getBudgetEndDate());

		budget1.setBudgetPeriod(period);
		budget1.setDescription(budgetRequest.getDescription());
		budget1.setUser(TestModels.aUSer());
		return budget1;
	}*/
}