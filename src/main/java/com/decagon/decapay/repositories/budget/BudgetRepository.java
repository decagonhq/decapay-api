package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long>, BudgetRepositoryCustom {


    @Query("select b from Budget b " +
            "left join fetch b.user u " +
            "where b.id=?1 " +
            "and b.auditSection.delF <> '1' ")
    Optional<Budget> findBudgetDetailsById(Long budgetId);

    @Query("select b from Budget b " +
            "left join b.budgetLineItems i " +
            "left join i.expenses e " +
            "where b.id = ?1 and b.user.id = ?2 " +
            "and b.auditSection.delF = '0' ")
    Optional<Budget> findBudgetByIdAndUserId(Long id, Long userId);

    @Query("select (count(b)>0) from Budget b " +
            "left join b.budgetLineItems i " +
            "left join i.expenses e " +
            "where b.id = ?1 and b.user.id = ?2 " +
            "and b.auditSection.delF = '0' " +
            "and e.transactionDate >= date(?3) and e.transactionDate <= date(?4)  ")
    boolean expenseExistsBetweenStartAndEndPeriod(Long id, Long userId, String startDate, String endDate);

}
