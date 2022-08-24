package com.decagon.decapay.controller.budget;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.budget.BudgetCategoryResponseDto;
import com.decagon.decapay.dto.budget.BudgetExpensesResponseDto;
import com.decagon.decapay.dto.budget.CreateBudgetLineItemDto;
import com.decagon.decapay.dto.budget.EditBudgetLineItemDto;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.model.budget.Expenses;
import com.decagon.decapay.service.budget.BudgetService;
import com.decagon.decapay.service.budget.expenses.ExpensesService;
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

import java.util.Collection;
import java.util.List;

import static com.decagon.decapay.constants.ResponseMessageConstants.*;

@Tag(name = "Budget Expenses Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class BudgetExpensesController {
    private final ExpensesService expensesService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESOURCE_RETRIEVED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST,content = @Content),
            @ApiResponse(responseCode = "403", description = NOT_AUTHORIZED,content = @Content)})
    @Operation(summary = "List Budget Expenses", description = "Returns lists of user's budget expenses successfully")
    @GetMapping("/budgets/{budgetId}/lineItems/{categoryId}/expenses")
    public ResponseEntity<ApiDataResponse<Collection<BudgetExpensesResponseDto>>> listBudgetCategories(@PathVariable Long budgetId, @PathVariable Long categoryId) {
        Collection<BudgetExpensesResponseDto> budgetExpensesResponse =expensesService.getListOfBudgetExpenses(budgetId, categoryId);
        return ApiResponseUtil.response(HttpStatus.OK, budgetExpensesResponse);
    }



}
