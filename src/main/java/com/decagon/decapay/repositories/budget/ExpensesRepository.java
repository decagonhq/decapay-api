package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpensesRepository extends JpaRepository<Expenses, Long> {

}
