package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.dto.budget.BudgetExpensesResponseDto;
import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.budget.Expenses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

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

    @Query("select e from Expenses e " +
            "join fetch e.budgetLineItem l " +
            "join fetch l.budget b " +
            "join fetch l.budgetCategory c " +
            "where e.id=?1 " +
            "and e.auditSection.delF <> '1' ")
    Optional<Expenses> findExpenseById(Long id);

    @Query("select e.id from Expenses e " +
            "join e.budgetLineItem.budget b " +
            "where b.id = ?1 " +
            "and b.auditSection.delF = '0' " +
            "and e.transactionDate < ?2 or e.transactionDate > ?3 ")
    Slice<Long> existsExpenseOutsideBudgetPeriod(Long id, LocalDate localDate, LocalDate localDate1, Pageable pageable);
}
