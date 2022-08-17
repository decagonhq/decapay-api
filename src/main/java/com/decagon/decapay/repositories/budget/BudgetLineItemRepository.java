package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.BudgetLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetLineItemRepository extends JpaRepository<BudgetLineItem, Long> {

    @Query("select b from BudgetLineItem b where b.budget.id = ?1 and b.budgetCategory.id = ?2")
    Optional<BudgetLineItem> findByBudgetIdAndBudgetCategoryId(Long budgetId, Long categoryId);
}
