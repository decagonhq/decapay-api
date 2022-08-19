package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expenses, Long> {

    boolean existsByBudgetLineItem_BudgetCategoryAndBudgetLineItem_Budget(BudgetCategory budgetCategory, Budget budget);

}
