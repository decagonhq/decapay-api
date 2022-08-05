package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.Budget;
import com.decagon.decapay.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

}
