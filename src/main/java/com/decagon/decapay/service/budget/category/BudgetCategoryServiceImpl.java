package com.decagon.decapay.service.budget.category;

import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.budget.BudgetCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetCategoryServiceImpl implements BudgetCategoryService {
    private final BudgetCategoryRepository budgetCategoryRepository;

    @Override
    public Optional<BudgetCategory> findCategoryByIdAndUser(Long budgetCategoryId, User user) {
        return this.budgetCategoryRepository.findByIdAndUser(budgetCategoryId, user);
    }
}
