package com.decagon.decapay.service.budget;

import com.decagon.decapay.dto.SearchCriteria;
import com.decagon.decapay.dto.budget.*;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.exception.*;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.BudgetLineItem;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.populator.CreateBudgetPopulator;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.budget.ExpenseRepository;
import com.decagon.decapay.service.budget.category.BudgetCategoryService;
import com.decagon.decapay.service.budget.periodHandler.AbstractBudgetPeriodHandler;
import com.decagon.decapay.service.currency.CurrencyService;
import com.decagon.decapay.utils.PageUtil;
import com.decagon.decapay.utils.UserInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BudgetServiceImpl implements BudgetService {
	private final BudgetRepository budgetRepository;
	private final CurrencyService currencyService;
	private final BudgetCategoryService budgetCategoryService;
	private final ExpenseRepository expenseRepository;
	private final UserInfoUtil userInfoUtil;

	public BudgetServiceImpl(final BudgetRepository budgetRepository,CurrencyService currencyService, BudgetCategoryService budgetCategoryService, UserInfoUtil userInfoUtil) {
		this.budgetRepository = budgetRepository;
		this.currencyService=currencyService;
		this.budgetCategoryService = budgetCategoryService;
		this.userInfoUtil=userInfoUtil;
		this.expenseRepository = expenseRepository;
	}

	@Transactional
	@Override
	public CreateBudgetResponseDTO createBudget(CreateBudgetRequestDTO budgetRequest, AbstractBudgetPeriodHandler budgetPeriodHandler) {

		User currentUser = this.userInfoUtil.getCurrAuthUser();

		Budget budget =this.createModelEntity(budgetRequest,budgetPeriodHandler);

		this.saveBudget(budget,currentUser);

		return new CreateBudgetResponseDTO(budget.getId());
	}

	private void saveBudget(Budget budget, User user) {
		//user.addBudget(budget);
		budget.setUser(user);
		budgetRepository.save(budget);
	}

	private Budget createModelEntity(CreateBudgetRequestDTO budgetRequest, AbstractBudgetPeriodHandler budgetPeriodHandler) {
		Budget budget=new Budget();
		CreateBudgetPopulator populator=new CreateBudgetPopulator();
		populator.setBudgetPeriodHandler(budgetPeriodHandler);
		populator.populate(budgetRequest,budget);
		return budget;
	}


	@Override
	public Page<BudgetResponseDto> getBudgets(int pageSize, int pageNo, List<SearchCriteria> searchCriterias) {

		Pageable pageable = PageUtil.normalisePageRequest(pageNo, pageSize);

		User currentUser = this.userInfoUtil.getCurrAuthUser();

		Page<BudgetResponseDto> budgets = budgetRepository.findBudgetsByUserId(pageable, currentUser.getId(), searchCriterias);

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
		User currentUser = this.userInfoUtil.getCurrAuthUser();

		Optional<Budget> optionalBudget = budgetRepository.findBudgetDetailsById(budgetId);

		if (optionalBudget.isEmpty()){
			throw new ResourceNotFoundException("Resource Not Found");
		}

		Budget budget = optionalBudget.get();

		if (!isCurrentUserOwnerOfBudget(currentUser,budget)){
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
		//convert budget line items
		Collection<ViewBudgetDto.LineItemDto> lineItems=convertBudgetLineItemToDto(budget.getBudgetLineItems());
		budgetViewDto.setLineItems(lineItems);
		return budgetViewDto;
	}

	private Collection<ViewBudgetDto.LineItemDto> convertBudgetLineItemToDto(Set<BudgetLineItem> budgetLineItems) {

		return budgetLineItems.stream().map(budgetLineItem -> {
			ViewBudgetDto.LineItemDto item=new ViewBudgetDto.LineItemDto();
			item.setBudgetId(budgetLineItem.getBudget().getId());
			item.setCategoryId(budgetLineItem.getBudgetCategory().getId());
			item.setCategory(budgetLineItem.getBudgetCategory().getTitle());
			item.setProjectedAmount(budgetLineItem.getProjectedAmount());
			item.setTotalAmountSpentSoFar(budgetLineItem.getTotalAmountSpentSoFar()==null?BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_DOWN):budgetLineItem.getTotalAmountSpentSoFar());
			item.setDisplayProjectedAmount(currencyService.formatAmount(budgetLineItem.getProjectedAmount()));
			item.setDisplayTotalAmountSpentSoFar(currencyService.formatAmount(budgetLineItem.getTotalAmountSpentSoFar()));
			BigDecimal percentageSpentSoFar = budgetLineItem.calculatePercentageAmountSpent();
			item.setPercentageSpentSoFar(percentageSpentSoFar);
			item.setDisplayPercentageSpentSoFar(percentageSpentSoFar+"%");
			return item;
		}).collect(Collectors.toList());

	}


	@Transactional
    @Override
    public IdResponseDto updateBudget(Long budgetId, CreateBudgetRequestDTO budgetRequestDto, AbstractBudgetPeriodHandler budgetPeriodHandler) {

		User currentUser = this.userInfoUtil.getCurrAuthUser();

		Budget budget = this.budgetRepository.findBudgetByIdAndUserId(budgetId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

		if (!isCurrentUserOwnerOfBudget(currentUser, budget)){
			log.error(String.format("This should be an hacking attempt, userid : %s, budgetId : %s", currentUser.getId(), budgetId));
			throw new InvalidRequestException("Invalid Request");
		}

        if (isNewBudgetAmountLessThanOriginalLineItemsTotalAmount(budgetRequestDto.getAmount(), budget)){
            throw new InvalidRequestException("Budget amount must be greater or equal to existing line items total amount");
        }

		if (transactionExistsOutsideOfNewBudgetPeriod(budgetRequestDto, budget, budgetPeriodHandler)) {
            throw new InvalidRequestException("Unable to update budget, transaction exists that are outside the new budget's start and end date");
        }

        this.updateBudget(budget, budgetRequestDto, budgetPeriodHandler);

		return new IdResponseDto(budget.getId());
    }

	private void updateBudget(Budget budget, CreateBudgetRequestDTO budgetRequestDto, AbstractBudgetPeriodHandler budgetPeriodHandler) {
		this.updateBudgetModel(budget, budgetRequestDto, budgetPeriodHandler);
	}

	private boolean isCurrentUserOwnerOfBudget(User user, Budget budget) {
		return user.getId().equals(budget.getUser().getId());
	}

	private boolean isNewBudgetAmountLessThanOriginalLineItemsTotalAmount(BigDecimal newProjectedAmount, Budget budget){
		return newProjectedAmount.compareTo(budget.calculateBudgetLineItemsTotalAmount()) < 0;
	}

	private boolean transactionExistsOutsideOfNewBudgetPeriod(CreateBudgetRequestDTO budgetRequestDto, Budget budget, AbstractBudgetPeriodHandler budgetPeriodHandler) {
		LocalDate[] targetdDateRange= budgetPeriodHandler.calculateBudgetDateRange(budgetRequestDto);
		return this.budgetRepository.expenseExistsForPeriod(budget.getId(), targetdDateRange[0], targetdDateRange[1]);

	}

    private void updateBudgetModel(Budget budget, CreateBudgetRequestDTO budgetRequestDto, AbstractBudgetPeriodHandler budgetPeriodHandler) {
		CreateBudgetPopulator populator=new CreateBudgetPopulator();
		populator.setBudgetPeriodHandler(budgetPeriodHandler);
		populator.populate(budgetRequestDto, budget);
    }

	@Override
	public CreateBudgetRequestDTO fetchBudget(Long budgetId) {

		User currentUser = this.userInfoUtil.getCurrAuthUser();

		Budget budget = this.budgetRepository.findBudgetByIdAndUserId(budgetId, currentUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

		if (!isCurrentUserOwnerOfBudget(currentUser, budget)){
			log.error(String.format("This should be an hacking attempt, userid : %s, budgetId : %s", currentUser.getId(), budgetId));
			throw new InvalidRequestException("Invalid Request");
		}

		return this.convertBudgetToResponseDTO(budget);
	}

	private CreateBudgetRequestDTO convertBudgetToResponseDTO(Budget budget) {
		AbstractBudgetPeriodHandler budgetPeriodHandler = AbstractBudgetPeriodHandler.getHandler(budget.getBudgetPeriod().name());

		CreateBudgetRequestDTO budgetRequestDto = new CreateBudgetRequestDTO();
		budgetRequestDto.setAmount(budget.getProjectedAmount());
		budgetRequestDto.setTitle(budget.getTitle());
		budgetRequestDto.setDescription(budget.getDescription());
		budgetRequestDto.setPeriod(budget.getBudgetPeriod().name());
		budgetPeriodHandler.setBudgetPeriodMetaData(budgetRequestDto, budget);
		return budgetRequestDto;
	}

	@Override
	@Transactional
	public IdResponseDto createLineItem(Long budgetId, BudgetLineItemDto budgetLineItemDto) {

		User currentUser = this.userInfoUtil.getCurrAuthUser();

		Budget budget = this.budgetRepository.findBudgetWithLineItems(budgetId, currentUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

		BudgetCategory category = this.budgetCategoryService.findCategoryByIdAndUser(budgetLineItemDto.getBudgetCategoryId(), currentUser)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));

		if(isBudgetLineItemExistsForCategory(budget.getBudgetLineItems(), category)){
			throw new ResourceConflictException("Budget line item already exists");
		}

		BigDecimal expectedLineItemsTotalAmountAfterSave = calculateExpectedNewTotalLineItemsAmountAfterSave(budget, budgetLineItemDto);

		if(isBudgetProjectedAmountLessThanLineItemsTotalAmountAfterSave(budget, expectedLineItemsTotalAmountAfterSave)){
			throw new InvalidRequestException(String.format("Sum of Line Item Projected amount {%s} Cannot be greater than budget total amount {%s} ", currencyService.formatAmount(expectedLineItemsTotalAmountAfterSave), budget.getProjectedAmount()));
		}

		this.saveBudgetLineItem(budget, category, budgetLineItemDto.getAmount());

		return new IdResponseDto(budget.getId());
	}

	private void saveBudgetLineItem(Budget budget, BudgetCategory category, BigDecimal amount) {
		budget.addBudgetLineItem(category, amount);
	}

	private boolean isBudgetProjectedAmountLessThanLineItemsTotalAmountAfterSave(Budget budget, BigDecimal lineItemsTotalAmount) {
		return budget.getProjectedAmount().compareTo(lineItemsTotalAmount) < 0;
	}

	/**
	 * Method Calculates The Sum of Expected Line Items Amount After Adding New Line Item
	 * @param budget
	 * @param budgetLineItemDto
	 * @return BudgetLineItemDto Amount if budget has no existing line item else return the sum of existing line items amount and new requested line item amount
	 */
	private BigDecimal calculateExpectedNewTotalLineItemsAmountAfterSave(Budget budget, BudgetLineItemDto budgetLineItemDto){
		if (budget.getBudgetLineItems().isEmpty()){
			return budgetLineItemDto.getAmount();
		}

		BigDecimal budgetTotalAmount = budget.calculateBudgetLineItemsTotalAmount();
		return budgetTotalAmount.add(budgetLineItemDto.getAmount());
	}


	private boolean isBudgetLineItemExistsForCategory(Collection<BudgetLineItem> budgetLineItems, BudgetCategory category) {
		return budgetLineItems
				.stream()
				.anyMatch(lineItem -> lineItem.getBudgetCategory().getId().equals(category.getId()));
	}

	@Override
	@Transactional
	public void updateLineItem(Long budgetId, Long categoryId, EditBudgetLineItemDto budgetLineItemDto) {
		User user = this.getAuthenticatedUser();

		Budget budget = this.budgetRepository.findBudgetWithLineItems(budgetId, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

		BudgetCategory category = this.budgetCategoryService.findCategoryByIdAndUser(categoryId, user)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));

		BudgetLineItem lineItem = getLineItem(budget, category);

		BigDecimal expectedLineItemsTotalAmountForNewEditRequestAfterSave = calculateExpectedNewTotalLineItemsAmountForNewEditRequestAfterSave(budget,lineItem,budgetLineItemDto);

		if(isBudgetProjectedAmountLessThanLineItemsTotalAmountAfterSave(budget, expectedLineItemsTotalAmountForNewEditRequestAfterSave)){
			throw new InvalidRequestException(String.format("Sum of Line Item Projected amount {%s} Cannot be greater than budget total amount {%s} ", currencyService.formatAmount(expectedLineItemsTotalAmountForNewEditRequestAfterSave), budget.getProjectedAmount()));
		}

		this.updateBudgetLineItem(lineItem, budgetLineItemDto.getAmount());

	}

	private void updateBudgetLineItem(BudgetLineItem lineItem, BigDecimal amount) {
		lineItem.setProjectedAmount(amount);
	}

	private BigDecimal calculateExpectedNewTotalLineItemsAmountForNewEditRequestAfterSave(Budget budget, BudgetLineItem oldLineItem, EditBudgetLineItemDto budgetLineItemDto){
		if (budget.getBudgetLineItems().isEmpty()){
			return budgetLineItemDto.getAmount();
		}
		BigDecimal budgetTotalAmount = budget.calculateBudgetLineItemsTotalAmount().subtract(oldLineItem.getProjectedAmount());
		return budgetTotalAmount.add(budgetLineItemDto.getAmount());
	}

	private BudgetLineItem getLineItem(Budget budget, BudgetCategory category) {
		BudgetLineItem budgetLineItem=budget.getBudgetLineItem(category);
		if( budgetLineItem== null){
			throw new ResourceNotFoundException("Budget Line item not found");
		}
		return budgetLineItem;
	}

	@Override
	@Transactional
	public void removeLineItem(Long budgetId, Long categoryId) {
		User user = this.getAuthenticatedUser();

		Budget budget = this.budgetRepository.findBudgetWithLineItems(budgetId, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

		BudgetCategory category = this.budgetCategoryService.findCategoryByIdAndUser(categoryId, user)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));

		BudgetLineItem lineItem = this.getLineItem(budget, category);

		if (isExpensesPresentForLineItem(lineItem)){
			throw new InvalidRequestException("Cannot delete line item with existing expenses");
		}

		this.removeLineItem(budget, category);
	}

	private boolean isExpensesPresentForLineItem(BudgetLineItem lineItem){
		return expenseRepository.existsByBudgetLineItem_BudgetCategoryAndBudgetLineItem_Budget(lineItem.getBudgetCategory(), lineItem.getBudget());
	}

	private void removeLineItem(Budget budget, BudgetCategory category) {
		budget.removeBudgetLineItem(category);
	}

}
