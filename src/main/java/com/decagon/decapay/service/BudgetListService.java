package com.decagon.decapay.service;


import com.decagon.decapay.dto.BudgetResponseDto;
import com.decagon.decapay.dto.SearchCriteria;
import org.springframework.data.domain.Page;

import java.util.List;


public interface BudgetListService {
    Page<BudgetResponseDto> getBudgets(int pageSize, int pageNo, List<SearchCriteria> searchCriterias);
}
