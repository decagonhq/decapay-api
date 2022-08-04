package com.decagon.decapay.repositories.budget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.decagon.decapay.model.budget.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

}