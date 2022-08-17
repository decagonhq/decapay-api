package com.decagon.decapay.controller.budget;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.budget.BudgetLineItemDto;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.service.budget.lineitem.BudgetLineItemService;
import com.decagon.decapay.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.decagon.decapay.constants.ResponseMessageConstants.*;

@Tag(name = "Budget Line Item Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class BudgetLineItemController {
    private final BudgetLineItemService budgetLineItemService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = LINE_ITEM_CREATED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST),
            @ApiResponse(responseCode = "403", description = NOT_AUTHORIZED),
            @ApiResponse(responseCode = "404", description = NOT_FOUND)})
    @Operation(summary = "Create Budget Line Item", description = "Create Budget Line Item")
    @PostMapping("/budgets/{budgetId}/lineItems")
    public ResponseEntity<ApiDataResponse<IdResponseDto>> createBudgetLineItem(@PathVariable Long budgetId, @RequestBody BudgetLineItemDto budgetLineItemDto) {
        return ApiResponseUtil.response(HttpStatus.OK, this.budgetLineItemService.createLineItem(budgetId, budgetLineItemDto), LINE_ITEM_CREATED_SUCCESSFULLY);
    }


}
