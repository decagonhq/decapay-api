package com.decagon.decapay.service.budget.lineitem;


import com.decagon.decapay.dto.budget.BudgetLineItemDto;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.exception.ResourceConflictException;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.exception.UnAuthorizedException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.budget.BudgetCategoryRepository;
import com.decagon.decapay.repositories.budget.BudgetLineItemRepository;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.UserInfo;
import com.decagon.decapay.utils.UserInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetLineItemServiceImpl implements BudgetLineItemService {
    private final UserInfoUtil userInfoUtil;
    private final BudgetRepository budgetRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final UserRepository userRepository;
    private final BudgetLineItemRepository budgetLineItemRepository;

    @Override
    public IdResponseDto createLineItem(BudgetLineItemDto budgetLineItemDto) {
        User user = this.getAuthenticatedUser();

        Budget budget = this.budgetRepository.findBudgetByIdAndUserId(budgetLineItemDto.getBudgetId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        BudgetCategory category = this.budgetCategoryRepository.findById(budgetLineItemDto.getBudgetCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        this.validateThatBudgetLineItemDoesNotExist(budget, category);

        if (!categoryBelongsToUser(user, category)) {
            throw new InvalidRequestException("You are not authorized to create budget line item");
        }

        if(!isProjectedAmountValid(budget, budgetLineItemDto)){
            throw new InvalidRequestException("Sum of Line Item Projected amount is greater than budget total amount");
        }

        budget = this.saveLineItem(budget, category, budgetLineItemDto);

        return new IdResponseDto(budget.getId());
    }

    private Budget saveLineItem(Budget budget, BudgetCategory category, BudgetLineItemDto dto){
        budget.addBudgetLineItem(category, dto.getAmount());
        return this.budgetRepository.save(budget);
    }

    private boolean isProjectedAmountValid(Budget budget, BudgetLineItemDto budgetLineItemDto) {
        if (budget.getBudgetLineItems().isEmpty() && budgetLineItemDto.getAmount().compareTo(budget.getProjectedAmount()) <= 0) {
            return true;
        }
        BigDecimal budgetTotalAmount = budget.calculateBudgetLineItemsTotalAmount();
        return budgetTotalAmount.add(budgetLineItemDto.getAmount()).compareTo(budget.getProjectedAmount()) <= 0;
    }


    private void validateThatBudgetLineItemDoesNotExist(Budget budget, BudgetCategory category) {
        this.budgetLineItemRepository.findByBudgetIdAndBudgetCategoryId(budget.getId(), category.getId())
                .ifPresent(budgetLineItem -> {
                    throw new ResourceConflictException("Budget line item already exists");
                });
    }

    private boolean categoryBelongsToUser(User user, BudgetCategory category) {
        return Objects.equals(user.getId(), category.getUser().getId());
    }

    private User getAuthenticatedUser() {
        UserInfo authenticatedUserInfo = this.userInfoUtil.authenticatedUserInfo();

        if (authenticatedUserInfo == null){
            throw new UnAuthorizedException("Authenticated User not found");
        }
        Optional<User> user = userRepository.findUserByEmail(authenticatedUserInfo.getUsername());
        if (user.isEmpty()){
            throw  new ResourceNotFoundException("User not found");
        }
        return user.get();
    }
}
