package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.dto.budget.BudgetCategoryResponseDto;
import com.decagon.decapay.model.budget.BudgetCategory;
import com.decagon.decapay.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import java.util.Optional;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Long> {

    @Query("select new com.decagon.decapay.dto.budget.BudgetCategoryResponseDto(b.id, b.title) " +
            "from BudgetCategory b " +
            "where b.user.id=?1 " +
            "AND b.auditSection.delF <> '1' ")
    List<BudgetCategoryResponseDto> findCategoriesByUserId(Long userId);

    Optional<BudgetCategory> findByIdAndUser(Long budgetCategoryId, User user);
}
