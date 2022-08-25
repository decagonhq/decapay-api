package com.decagon.decapay.controller.budget;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.budget.BudgetExpensesResponseDto;
import com.decagon.decapay.service.budget.BudgetService;
import com.decagon.decapay.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.decagon.decapay.dto.budget.ExpenseDto;
import com.decagon.decapay.dto.common.IdResponseDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.decagon.decapay.constants.ResponseMessageConstants.*;

@Tag(name = "Expense Controller")
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

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESOURCE_RETRIEVED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST,content = @Content),
            @ApiResponse(responseCode = "401", description = NOT_AUTHORIZED,content = @Content)})
    @Operation(summary = "End point to list user expenses for a budget line item", description = "Returns lists of user's  expenses successfully")
    @Parameters({
            @Parameter(name = "pageable", hidden = true),
            @Parameter(in = ParameterIn.QUERY
                    , description = "Page you want to retrieve (0..N)"
                    , name = "page"
                    , content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))),
            @Parameter(in = ParameterIn.QUERY
                    , description = "Number of records per page."
                    , name = "size"
                    , content = @Content(schema = @Schema(type = "integer", defaultValue = "10"))),
    })
    @GetMapping("/budgets/{budgetId}/lineItems/{categoryId}/expenses")
    public ResponseEntity<ApiDataResponse<Page<BudgetExpensesResponseDto>>> listExpenses(@PathVariable Long budgetId, @PathVariable Long categoryId,Pageable pageable) {
        Page<BudgetExpensesResponseDto> budgetExpensesResponse =budgetService.getListOfBudgetExpenses(budgetId, categoryId, pageable);
        return ApiResponseUtil.response(HttpStatus.OK, budgetExpensesResponse);
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = EXPENSE_REMOVED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST,content = @Content),
            @ApiResponse(responseCode = "401", description = NOT_AUTHORIZED,content = @Content),
            @ApiResponse(responseCode = "404", description = NOT_FOUND,content = @Content)})
    @Operation(summary = "Remove Expense", description = "Remove Expense")
    @DeleteMapping("/expenses/{expenseId}")
    public ResponseEntity<ApiDataResponse<Object>> removeExpense(@PathVariable Long expenseId) {
        this.budgetService.removeExpense(expenseId);
        return ApiResponseUtil.response(HttpStatus.NO_CONTENT, EXPENSE_REMOVED_SUCCESSFULLY);
    }



}
