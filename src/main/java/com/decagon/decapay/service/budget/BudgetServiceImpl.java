package com.decagon.decapay.service.budget;

import com.decagon.decapay.dto.SearchCriteria;
import com.decagon.decapay.dto.budget.BudgetResponseDto;
import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.dto.budget.CreateBudgetResponseDTO;
import com.decagon.decapay.dto.budget.ViewBudgetDto;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.exception.InvalidCredentialException;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.exception.UnAuthorizedException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.payloads.request.budget.UpdateBudgetRequestDto;
import com.decagon.decapay.populator.CreateBudgetPopulator;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.security.UserInfo;
import com.decagon.decapay.service.budget.periodHandler.BudgetPeriodHandler;
import com.decagon.decapay.service.currency.CurrencyService;
import com.decagon.decapay.utils.PageUtil;
import com.decagon.decapay.utils.UserInfoUtills;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetServiceImpl implements BudgetService {
	private final BudgetRepository budgetRepository;
	private final UserRepository userRepository;
	private final CurrencyService currencyService;
	//TODO: replace with  userService or userInfo component
	private final  CustomUserDetailsService userDetailsService;
	private final UserInfoUtills userInfoUtills;

	public BudgetServiceImpl(final BudgetRepository budgetRepository, final CustomUserDetailsService userDetailsService
			,UserRepository userRepository,CurrencyService currencyService,UserInfoUtills userInfoUtills) {
		this.budgetRepository = budgetRepository;
		this.userDetailsService = userDetailsService;
		this.userRepository =userRepository;
		this.currencyService=currencyService;
		this.userInfoUtills=userInfoUtills;
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

	private Budget createModelEntity(CreateBudgetRequestDTO budgetRequest, BudgetPeriodHandler budgetPeriodHandler) {

		Budget budget=new Budget();
		CreateBudgetPopulator populator=new CreateBudgetPopulator();
		populator.setBudgetPeriodHandler(budgetPeriodHandler);
		populator.populate(budgetRequest,budget);
		return budget;
	}


	@Override
	public Page<BudgetResponseDto> getBudgets(int pageSize, int pageNo, List<SearchCriteria> searchCriterias) {

		Pageable pageable = PageUtil.normalisePageRequest(pageNo, pageSize);
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userRepository.findUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new InvalidCredentialException("Invalid Credentials"));
		Page<BudgetResponseDto> budgets = budgetRepository.findBudgetsByUserId(pageable, user.getId(), searchCriterias);
		Budget budget1 = new Budget();
		return budgets.map(budgetResponseDto -> {
			budgetResponseDto.setDisplayTotalAmountSpentSoFar(currencyService.formatAmount(budgetResponseDto.getTotalAmountSpentSoFar()));
			budgetResponseDto.setDisplayProjectedAmount(currencyService.formatAmount(budgetResponseDto.getProjectedAmount()));
			budget1.setTotalAmountSpentSoFar(budgetResponseDto.getTotalAmountSpentSoFar());
			budget1.setProjectedAmount(budgetResponseDto.getProjectedAmount());
			BigDecimal percentageSpentSoFar = budget1.calculatePercentageAmountSpent();
			budgetResponseDto.setPercentageSpentSoFar(percentageSpentSoFar.doubleValue());
			budgetResponseDto.setDisplayPercentageSpentSoFar(percentageSpentSoFar + "%");
			return budgetResponseDto;
		});
	}

	@Override
	public ViewBudgetDto viewBudgetDetails(Long budgetId) {
		User user = this.getAuthenticatedUser();

		Optional<Budget> optionalBudget = budgetRepository.findBudgetDetailsById(budgetId);

		if (optionalBudget.isEmpty()){
			throw new ResourceNotFoundException("Resource Not Found");
		}

		Budget budget = optionalBudget.get();
		if (!user.getId().equals(budget.getUser().getId())){
			throw new InvalidRequestException("Invalid Request");
		}
		return this.convertBudgetViewDto(budget);
	}


	private ViewBudgetDto convertBudgetViewDto(Budget budget) {

		ViewBudgetDto budgetViewDto = new ViewBudgetDto();
		budgetViewDto.setId(budget.getId());
		budgetViewDto.setTitle(budget.getTitle());
		budgetViewDto.setBudgetPeriod(budget.getBudgetPeriod().name());
		budgetViewDto.setNotificationThreshold(budget.getNotificationThreshold());
		budgetViewDto.setProjectedAmount(budget.getProjectedAmount());
		budgetViewDto.setTotalAmountSpentSoFar(budget.getTotalAmountSpentSoFar());
		budgetViewDto.setEndDate(budget.getBudgetEndDate());
		budgetViewDto.setStartDate(budget.getBudgetStartDate());
		budgetViewDto.setDisplayEndDate(budget.getBudgetEndDate());
		budgetViewDto.setDisplayStartDate(budget.getBudgetStartDate());
		budgetViewDto.setDisplayProjectedAmount(currencyService.formatAmount(budget.getProjectedAmount()));
		budgetViewDto.setDisplayTotalAmountSpentSoFar(currencyService.formatAmount(budget.getTotalAmountSpentSoFar()));
		BigDecimal percentageSpentSoFar = budget.calculatePercentageAmountSpent();
		budgetViewDto.setPercentageSpentSoFar(percentageSpentSoFar);
		budgetViewDto.setDisplayPercentageSpentSoFar(percentageSpentSoFar+"%");
		return budgetViewDto;
	}

	public User getAuthenticatedUser() {

		UserInfo authenticatedUserInfo = this.userInfoUtills.authenticationUserInfo();
		if (authenticatedUserInfo == null){
			throw new UnAuthorizedException("Authenticated User not found");
		}
		Optional<User> user = userRepository.findUserByEmail(authenticatedUserInfo.getUsername());
		if (user.isEmpty()){
			throw  new ResourceNotFoundException("User not found");
		}
		return user.get();
	}

	@Transactional
    @Override
    public IdResponseDto updateBudget(Long budgetId, CreateBudgetRequestDTO budgetRequestDto, BudgetPeriodHandler budgetPeriodHandler) {
        User user = this.getAuthenticatedUser();

		Budget budget = this.budgetRepository.findBudgetDetailsById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

		if (!user.getId().equals(budget.getUser().getId())){
			throw new InvalidRequestException("Invalid Request");
		}

        if (budgetRequestDto.getAmount().compareTo(budget.calculateBudgetLineItemsTotalAmount()) != 0){
            throw new InvalidRequestException("Budget amount cannot be less/greater than line items total amount edit line item and try again");
        }

		LocalDate[] targetdDateRange= budgetPeriodHandler.calculateBudgetDateRange(budgetRequestDto);
        boolean transactionExistsOuOfPeriod = this.budgetRepository.expenseExistsForPeriod(budgetId,targetdDateRange[0], targetdDateRange[1]);

		if (transactionExistsOuOfPeriod) {
            throw new InvalidRequestException("Budget period cannot be outside of the current period");
        }

        return this.processBudgetUpdate(budget, budgetRequestDto, budgetPeriodHandler);
    }

    private IdResponseDto processBudgetUpdate(Budget budget, CreateBudgetRequestDTO budgetRequestDto, BudgetPeriodHandler budgetPeriodHandler) {
		CreateBudgetPopulator populator=new CreateBudgetPopulator();
		populator.setBudgetPeriodHandler(budgetPeriodHandler);
		budget = populator.populate(budgetRequestDto, budget);
		budget = this.budgetRepository.save(budget);
        return new IdResponseDto(budget.getId());
    }

}
