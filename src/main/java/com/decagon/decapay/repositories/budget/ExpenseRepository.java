package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.BudgetLineItem;
import com.decagon.decapay.model.budget.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ExpenseRepository extends JpaRepository<Expenses, Long> {

    boolean existsByBudgetLineItem_BudgetCategoryAndBudgetLineItem_Budget(BudgetCategory budgetCategory, Budget budget);

    @Query("select sum(e.amount) from Expenses e where e.budgetLineItem = ?1")
    BigDecimal sumByBudgetLineItem(BudgetLineItem budgetLineItem);

}
