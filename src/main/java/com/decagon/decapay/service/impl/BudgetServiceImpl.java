package com.decagon.decapay.service.impl;

import com.decagon.decapay.dto.CreateBudgetRequestDTO;
import com.decagon.decapay.dto.CreateBudgetResponseDTO;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.populator.CreateBudgetPopulator;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.service.BudgetService;
import com.decagon.decapay.service.budget.period.BudgetPeriodHandler;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class BudgetServiceImpl implements BudgetService {
	private final BudgetRepository budgetRepository;

	//TODO: replace with  userService or userInfo component
	private final  CustomUserDetailsService userDetailsService;

	public BudgetServiceImpl(final BudgetRepository budgetRepository, final CustomUserDetailsService userDetailsService) {
		this.budgetRepository = budgetRepository;
		this.userDetailsService = userDetailsService;
	}

	@Transactional
	@Override
	public CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest, BudgetPeriodHandler budgetPeriodHandler) {

		User user = userDetailsService.getLoggedInUser();
		Budget budget =this.createModelEntity(budgetRequest,budgetPeriodHandler);
		this.saveBudget(budget,user);
		return new CreateBudgetResponseDTO(budget.getId());
	}

	private void saveBudget(Budget budget, User user) {
		//user.addBudget(budget);
		budget.setUser(user);
		budgetRepository.save(budget);
	}

	private Budget createModelEntity(CreateBudgetRequestDTO budgetRequest,BudgetPeriodHandler budgetPeriodHandler) {
		Budget budget=new Budget();
		CreateBudgetPopulator populator=new CreateBudgetPopulator();
		populator.setBudgetPeriodHandler(budgetPeriodHandler);
		populator.populate(budgetRequest,budget);
		return budget;
	}


/*
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
			//budgetRequest.isValidForCustomPeriod();
			//budget.setBudgetStartDate(budgetRequest.getBudgetStartDate());
			//budget.setBudgetEndDate(budgetRequest.getBudgetEndDate());
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
	}*/
}
