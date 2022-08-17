package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Long> {

    Optional<BudgetCategory> findByIdAndUser(Long budgetCategoryId, User user);
}
