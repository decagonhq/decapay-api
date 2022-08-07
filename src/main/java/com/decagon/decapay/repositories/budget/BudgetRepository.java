package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long>, BudgetRepositoryCustom {


    @Query("select b from Budget b " +
            "left join fetch b.user u " +
            "where b.id=?1 " +
            "and b.auditSection.delF <> '1' ")
    Optional<Budget> findBudgetDetailsById(Long budgetId);

}
