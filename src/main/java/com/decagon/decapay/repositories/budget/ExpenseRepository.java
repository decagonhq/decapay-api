package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.dto.budget.BudgetExpensesResponseDto;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.Expenses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ExpenseRepository extends JpaRepository<Expenses, Long> {

    boolean existsByBudgetLineItem_BudgetCategoryAndBudgetLineItem_Budget(BudgetCategory budgetCategory, Budget budget);

    @Query("select new com.decagon.decapay.dto.budget.BudgetExpensesResponseDto(e.id, e.amount, e.description, e.transactionDate) " +
            "from Expenses e " +
            "join e.budgetLineItem b " +
            "join b.budget bt " +
            "join b.budgetCategory bc " +
            "where bt.id =?1 and bc.id=?2 order by e.transactionDate desc " )
    Page<BudgetExpensesResponseDto> fetchExpenses(Long budgetId, Long categoryId, Pageable pageable);
}
