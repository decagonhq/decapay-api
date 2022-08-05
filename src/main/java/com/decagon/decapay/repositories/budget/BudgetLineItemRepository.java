package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.model.budget.BudgetLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BudgetLineItemRepository extends JpaRepository<BudgetLineItem, Long> {
}
