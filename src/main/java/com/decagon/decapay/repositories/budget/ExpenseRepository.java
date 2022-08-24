package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ExpenseRepository extends JpaRepository<Expenses, Long> {

    boolean existsByBudgetLineItem_BudgetCategoryAndBudgetLineItem_Budget(BudgetCategory budgetCategory, Budget budget);

    @Query("select ex from Expenses ex " +
            "left join fetch ex.budgetLineItem b " +
            "left join fetch b.budget bt " +
            "left join fetch b.budgetCategory bc " +
            "where bt.id =?1 and bc.id=?2")
    Collection<Expenses> fetchExpensesDetails(Long id, Long id1);
}
