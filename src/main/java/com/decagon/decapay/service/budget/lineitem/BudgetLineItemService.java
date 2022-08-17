package com.decagon.decapay.service.budget.lineitem;

import com.decagon.decapay.dto.budget.BudgetLineItemDto;
import com.decagon.decapay.dto.common.IdResponseDto;

public interface BudgetLineItemService {
    IdResponseDto createLineItem(BudgetLineItemDto budgetLineItemDto);
}
