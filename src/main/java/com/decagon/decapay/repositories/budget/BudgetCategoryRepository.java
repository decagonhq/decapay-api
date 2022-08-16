package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.BudgetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Long> {
}
