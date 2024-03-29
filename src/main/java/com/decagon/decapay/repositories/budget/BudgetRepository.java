package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long>, BudgetRepositoryCustom {


    @Query("select b from Budget b " +
            "left join fetch b.user u " +
            "left join fetch b.budgetLineItems i " +
            "left join fetch i.budgetCategory " +
            "where b.id=?1 " +
            "and b.auditSection.delF <> '1' ")
    Optional<Budget> findBudgetDetailsById(Long budgetId);//TODO: add user id in query filter

    @Query("select b from Budget b " +
            "left join fetch b.budgetLineItems i " +
            "where b.id = ?1 and b.user.id = ?2 " +
            "and b.auditSection.delF = '0' ")
    Optional<Budget> findBudgetByIdAndUserId(Long id, Long userId);//TODO: rename method

    //todo: move to expense repository

    @Query("select b from Budget b " +
           "left join fetch b.budgetLineItems i " +
           "left join fetch i.budgetCategory " +
           "where b.id = ?1 and b.user.id = ?2 " +
           "and b.auditSection.delF = '0' ")
    Optional<Budget> findBudgetWithLineItems(Long budgetId, Long userId);
}
