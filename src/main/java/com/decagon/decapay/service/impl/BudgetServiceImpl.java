package com.decagon.decapay.service.impl;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

import java.time.DayOfWeek;
import java.time.LocalDate;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.decagon.decapay.dto.CreateBudgetRequestDTO;
import com.decagon.decapay.dto.CreateBudgetResponseDTO;
import com.decagon.decapay.enumTypes.BudgetPeriod;
import com.decagon.decapay.enumTypes.UserStatus;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.service.BudgetService;

@Service
public class BudgetServiceImpl implements BudgetService {

	private final BudgetRepository budgetRepository;
	private final  CustomUserDetailsService userDetailsService;

	public BudgetServiceImpl(final BudgetRepository budgetRepository, final CustomUserDetailsService userDetailsService) {
		this.budgetRepository = budgetRepository;
		this.userDetailsService = userDetailsService;
	}

	@Override
	@Transactional
	public CreateBudgetResponseDTO createBudget(final CreateBudgetRequestDTO budgetRequest) {
		User user = userDetailsService.getLoggedInUser();

		if (UserStatus.ACTIVE != user.getUserStatus()) {
			throw new InvalidRequestException("Cannot create budget for inactive user");
		}

		Budget budget = new Budget();
		budget.setTitle(budgetRequest.getTitle());
		budget.setProjectedAmount(budgetRequest.getAmount());

		BudgetPeriod period = BudgetPeriod.valueOf(budgetRequest.getPeriod());

		if (BudgetPeriod.CUSTOM == period ){
			budgetRequest.isValidForCustomPeriod();
			budget.setBudgetStartDate(budgetRequest.getBudgetStartDate());
			budget.setBudgetEndDate(budgetRequest.getBudgetEndDate());
		} else {
			budget.setBudgetStartDate(LocalDate.now());
			budget.setBudgetEndDate(getEndDateFromPeriod(period));
		}
		budget.setBudgetPeriod(period);
		budget.setDescription(budgetRequest.getDescription());
		budget.setUser(user);

		budget = budgetRepository.save(budget);

		return new CreateBudgetResponseDTO(budget.getId());
	}

	private LocalDate getEndDateFromPeriod(final BudgetPeriod period) {
		LocalDate now = LocalDate.now();
		return switch (period) {
			case DAILY -> now;
			case WEEKLY -> now.with(DayOfWeek.SATURDAY);
			case MONTHLY -> now.with(lastDayOfMonth());
			default -> now.with(lastDayOfYear());
		};
	}
}
