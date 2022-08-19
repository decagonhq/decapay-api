package com.decagon.decapay.controller.budget;


import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.budget.BudgetCategoryResponseDto;
import com.decagon.decapay.dto.budget.CreateBudgetCategoryDto;
import com.decagon.decapay.dto.budget.CreateBudgetResponseDTO;
import com.decagon.decapay.service.budget.category.BudgetCategoryService;
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
import java.util.List;

import static com.decagon.decapay.constants.ResponseMessageConstants.*;


@Tag(name = "Budget Category Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class BudgetCategoryController {

    private final BudgetCategoryService budgetCategoryService;


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESOURCE_RETRIEVED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST,content = @Content),
            @ApiResponse(responseCode = "403", description = NOT_AUTHORIZED,content = @Content)})
    @Operation(summary = "List Budget Categories", description = "Returns lists of user's budget category successfully")
    @GetMapping("/budget_categories")
    public ResponseEntity<ApiDataResponse<List<BudgetCategoryResponseDto>>> listBudgetCategories() {
        List<BudgetCategoryResponseDto> budgetCategoryResponseDtos =
                budgetCategoryService.getListOfBudgetCategories();
        return ApiResponseUtil.response(HttpStatus.OK, budgetCategoryResponseDtos);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = BUDGET_CATEGORY_SUCCESSFULLY_CREATED),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST,content = @Content),
            @ApiResponse(responseCode = "403", description = REQUEST_FORBIDDEN,content = @Content)})
    @Operation(summary = "Create budget category", description = "Create new user budget category with the mandatory field.")
    @PostMapping("/budget_categories")
    public ResponseEntity<ApiDataResponse<CreateBudgetResponseDTO>> createBudgetCategory(@Valid @RequestBody CreateBudgetCategoryDto request) {
        return ApiResponseUtil.response(HttpStatus.CREATED, budgetCategoryService.createBudgetCategory(request),BUDGET_CATEGORY_SUCCESSFULLY_CREATED);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = BUDGET_CATEGORY_UPDATED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST,content = @Content),
            @ApiResponse(responseCode = "403", description = NOT_AUTHORIZED,content = @Content),
            @ApiResponse(responseCode = "404", description = NOT_FOUND,content = @Content)})
    @Operation(summary = "Update Budget Category", description = "Update User Budget Category")
    @PutMapping("/budget_categories/{categoryId}")
    public ResponseEntity<ApiDataResponse<Object>> updateBudgetCategory(@PathVariable Long categoryId, @Valid @RequestBody CreateBudgetCategoryDto request) {
        budgetCategoryService.updateBudgetCategory(categoryId, request);
        return ApiResponseUtil.response(HttpStatus.OK, BUDGET_CATEGORY_UPDATED_SUCCESSFULLY);
    }
}
