package com.decagon.decapay.service.budget;

import com.decagon.decapay.DTO.SearchCriteria;
import com.decagon.decapay.DTO.budget.BudgetResponseDto;
import com.decagon.decapay.DTO.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.DTO.budget.CreateBudgetResponseDTO;
import com.decagon.decapay.DTO.budget.ViewBudgetDto;
import com.decagon.decapay.DTO.common.IdResponseDto;
import com.decagon.decapay.exception.InvalidCredentialException;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.exception.UnAuthorizedException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetPeriod;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.decagon.decapay.utils.CustomDateUtil.*;
import static java.time.temporal.TemporalAdjusters.*;

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

    @Override
    public IdResponseDto updateBudget(Long userId, Long budgetId, UpdateBudgetRequestDto budgetRequestDto) {
        User user = this.getAuthenticatedUser();

        if (!Objects.equals(user.getId(), userId)) {
            throw new UnAuthorizedException("You are not authorized to update this budget");
        }


        Budget budget = this.budgetRepository.findBudgetByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        this.processBudgetsBasedOnPeriod(budgetRequestDto, budget);

        System.out.println("@@@@@@@@@ impl start date: " + budgetRequestDto.getBudgetStartDate());
        System.out.println("@@@@@@@@@ impl end date: " + budgetRequestDto.getBudgetEndDate());


        boolean isExpenseTransactionDateWithinPeriod = this.budgetRepository.expenseExistsBetweenStartAndEndPeriod(budgetId, userId, budgetRequestDto.getBudgetStartDate(), budgetRequestDto.getBudgetEndDate());

        System.out.println("####### isExpenseTransactionDateWithinPeriod: " + isExpenseTransactionDateWithinPeriod);

        if (!isExpenseTransactionDateWithinPeriod) {
            throw new InvalidRequestException("Budget period cannot be outside of the current period");
        }

        if (!Objects.equals(budgetRequestDto.getTotalAmountSpentSoFar(), budget.getTotalAmountSpentSoFar())){
            throw new InvalidRequestException("Budget amount cannot be less/greater than line items total amount edit line item and try again");
        }

        return this.processBudgetUpdate(budget, budgetRequestDto);

    }

    private IdResponseDto processBudgetUpdate(Budget budget, UpdateBudgetRequestDto budgetRequestDto) {
        budget.setTitle(budgetRequestDto.getTitle());
        budget.setProjectedAmount(budgetRequestDto.getProjectedAmount());
        budget.setTotalAmountSpentSoFar(budgetRequestDto.getTotalAmountSpentSoFar());
        budget = this.budgetRepository.save(budget);
        return new IdResponseDto(budget.getId());
    }

    private void processBudgetsBasedOnPeriod(UpdateBudgetRequestDto requestDto, Budget budget){
        switch (BudgetPeriod.valueOf(requestDto.getBudgetPeriod())) {
            case DAILY -> processDailyBudget(requestDto, budget);
            case WEEKLY -> processWeeklyBudget(requestDto, budget);
            case MONTHLY -> processMonthlyBudget(requestDto, budget);
            case ANNUAL -> processAnnualBudget(requestDto, budget);
            case CUSTOM -> processCustomBudget(requestDto, budget);
            default -> throw new InvalidRequestException("Invalid budget period");
        }
    }

    private void processAnnualBudget(UpdateBudgetRequestDto budgetRequestDto, Budget budget) {
        this.validateInput(budgetRequestDto.getYear(), "year");

        short year = budgetRequestDto.getYear();

        budgetRequestDto.setBudgetStartDate(getDateFromYear(year, firstDayOfYear()).toString());
        budgetRequestDto.setBudgetEndDate(getDateFromYear(year, lastDayOfYear()).toString());
        this.processBudgetPeriodUpdate(budgetRequestDto, budget);
    }



    private void processMonthlyBudget(UpdateBudgetRequestDto budgetRequestDto, Budget budget) {
        this.validateInput(budgetRequestDto.getYear(), "year");
        this.validateInput(budgetRequestDto.getMonth(), "month");

        short year = budgetRequestDto.getYear();
        short month = budgetRequestDto.getMonth();

        budgetRequestDto.setBudgetStartDate(getDateFromMonth(year, month, firstDayOfMonth()).toString());
        budgetRequestDto.setBudgetEndDate(getDateFromMonth(year, month, lastDayOfMonth()).toString());

        this.processBudgetPeriodUpdate(budgetRequestDto, budget);
    }

    private void processWeeklyBudget(UpdateBudgetRequestDto budgetRequestDto, Budget budget) {
        this.validateInput(budgetRequestDto.getYear(), "year");
        this.validateInput(budgetRequestDto.getMonth(), "month");
        this.validateInput(budgetRequestDto.getWeek(), "week");

        short year = budgetRequestDto.getYear();
        short month = budgetRequestDto.getMonth();
        short week = budgetRequestDto.getWeek();

        budgetRequestDto.setBudgetStartDate(getDateFromWeek(year, month, week, (short) 1).toString());
        budgetRequestDto.setBudgetEndDate(getDateFromWeek(year, month, week, (short) 7).toString());
        this.processBudgetPeriodUpdate(budgetRequestDto, budget);

    }

    private void validateInput(int input, String name) {
        if (Optional.of(input).isEmpty()) {
            throw new InvalidRequestException(String.format("%s is required", name));
        }
    }


    private void processCustomBudget(UpdateBudgetRequestDto budgetRequestDto, Budget budget) {
        this.validateDate(budgetRequestDto);
//        if(!budgetRequestDto.getBudgetEndDate().i(budgetRequestDto.getBudgetStartDate())){
//            throw new InvalidRequestException("End date must be after start date");
//        }
        this.processBudgetPeriodUpdate(budgetRequestDto, budget);
    }

    private void processDailyBudget(UpdateBudgetRequestDto budgetRequestDto, Budget budget) {
        this.validateDate(budgetRequestDto);
        if (budgetRequestDto.getBudgetStartDate() != budgetRequestDto.getBudgetEndDate()){
            throw new InvalidRequestException("Start date and end date must be the same for daily budget");
        }
        this.processBudgetPeriodUpdate(budgetRequestDto, budget);

    }

    private void processBudgetPeriodUpdate(UpdateBudgetRequestDto budgetRequestDto, Budget budget) {
        budget.setBudgetPeriod(BudgetPeriod.valueOf(budgetRequestDto.getBudgetPeriod()));
//        budget.setBudgetStartDate(budgetRequestDto.getBudgetStartDate());
//        budget.setBudgetEndDate(budgetRequestDto.setBudgetEndDate());
    }

    private void validateDate(UpdateBudgetRequestDto budgetRequestDto) {
        if(budgetRequestDto.getBudgetStartDate() == null || budgetRequestDto.getBudgetEndDate() == null) {
            throw new InvalidRequestException("Start and end date are required for daily budget");
        }
    }

}
