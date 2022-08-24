package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expenses, Long> {

    boolean existsByBudgetLineItem_BudgetCategoryAndBudgetLineItem_Budget(BudgetCategory budgetCategory, Budget budget);

    @Query("select e from Expenses e " +
            "join fetch e.budgetLineItem l " +
            "join fetch l.budget b " +
            "join fetch l.budgetCategory c " +
            "where e.id=?1 " +
            "and e.auditSection.delF <> '1' ")
    Optional<Expenses> findExpenseById(Long id);
}
