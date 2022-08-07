package com.decagon.decapay.service.impl;


import com.decagon.decapay.dto.BudgetResponseDto;
import com.decagon.decapay.dto.SearchCriteria;
import com.decagon.decapay.exception.InvalidCredentialException;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.budget.BudgetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.service.BudgetListService;
import com.decagon.decapay.service.currency.CurrencyService;
import com.decagon.decapay.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@RequiredArgsConstructor
@Service
public class BudgetListServiceImpl implements BudgetListService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CurrencyService currencyService;

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
}
