package com.decagon.decapay.controller.budget;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.budget.ExpenseDto;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.service.budget.BudgetService;
import com.decagon.decapay.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.decagon.decapay.constants.ResponseMessageConstants.*;

@Tag(name = "Expenses Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class ExpenseController {
    private final BudgetService budgetService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = EXPENSE_CREATED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST,content = @Content),
            @ApiResponse(responseCode = "403", description = NOT_AUTHORIZED,content = @Content),
            @ApiResponse(responseCode = "404", description = NOT_FOUND,content = @Content)})
    @Operation(summary = "Create Expense", description = "Create Expense")
    @PostMapping("/budgets/{budgetId}/lineItems/{categoryId}/expenses")
    public ResponseEntity<ApiDataResponse<IdResponseDto>> createExpense(@PathVariable Long budgetId, @PathVariable Long categoryId, @Valid @RequestBody ExpenseDto expenseDto) {
        return ApiResponseUtil.response(HttpStatus.OK, this.budgetService.createExpense(budgetId, categoryId, expenseDto), EXPENSE_CREATED_SUCCESSFULLY);
    }
}
