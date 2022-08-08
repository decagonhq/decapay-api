package com.decagon.decapay.repositories.budget;

import com.decagon.decapay.dto.budget.BudgetResponseDto;
import com.decagon.decapay.dto.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BudgetRepositoryCustom {
    Page<BudgetResponseDto> findBudgetsByUserId(Pageable pageable, Long id, List<SearchCriteria> searchCriterias);
}
