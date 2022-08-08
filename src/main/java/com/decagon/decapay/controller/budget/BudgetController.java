package com.decagon.decapay.controller.budget;


import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.SearchCriteria;
import com.decagon.decapay.dto.budget.BudgetResponseDto;
import com.decagon.decapay.dto.budget.CreateBudgetRequestDTO;
import com.decagon.decapay.dto.budget.CreateBudgetResponseDTO;
import com.decagon.decapay.dto.budget.ViewBudgetDto;
import com.decagon.decapay.service.budget.BudgetService;
import com.decagon.decapay.service.budget.periodHandler.BudgetPeriodHandler;
import com.decagon.decapay.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static com.decagon.decapay.constants.ResponseMessageConstants.*;

@Tag(name = "Budget Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class BudgetController {

    private final BudgetService budgetService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = BUDGET_SUCCESSFULLY_CREATED),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST),
            @ApiResponse(responseCode = "403", description = REQUEST_FORBIDDEN)})
    @Operation(summary = "Create budget", description = "Create new user budget for with all mandatory fields.")
    @PostMapping("/budgets")
    public ResponseEntity<ApiDataResponse<CreateBudgetResponseDTO>> createBudget(@Valid @RequestBody CreateBudgetRequestDTO createBudgetRequest) {
        //todo: use strategy
        BudgetPeriodHandler budgetPeriodHandler = BudgetPeriodHandler.getHandler(createBudgetRequest.getPeriod());
        this.validateRequest(createBudgetRequest, budgetPeriodHandler);

        return ApiResponseUtil.response(HttpStatus.CREATED, budgetService.createBudget(createBudgetRequest, budgetPeriodHandler),
                BUDGET_SUCCESSFULLY_CREATED);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RESOURCE_RETRIEVED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST),
            @ApiResponse(responseCode = "404", description = NOT_FOUND)})
    @Operation(summary = "View Budget", description = "View Budget Details")
    @GetMapping("/budgets/{budgetId}")
    public ResponseEntity<ApiDataResponse<ViewBudgetDto>> fetchBudgetDetails(@PathVariable Long budgetId) {
        ViewBudgetDto budgetDto = this.budgetService.viewBudgetDetails(budgetId);
        return ApiResponseUtil.response(HttpStatus.OK, budgetDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RETURN_BUDGET_LISTS_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST),
            @ApiResponse(responseCode = "404", description = NOT_FOUND)})
    @Operation(summary = "Returns user's budget list successfully")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY
                    , description = "Page you want to retrieve (0..N)"
                    , name = "page"
                    , content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))),
            @Parameter(in = ParameterIn.QUERY
                    , description = "Number of records per page."
                    , name = "size"
                    , content = @Content(schema = @Schema(type = "integer", defaultValue = "10"))),
            @Parameter(in = ParameterIn.QUERY
                    , description = """
                    past | current | upcomming  \s

                    When set to 'past', past budgets will be listed, when set to 'upcomming', will list future budgets, when 'current', only active budgets wil be listed, when empty or not present all budgets will be listed. Default to empty value"""
                    , name = "state"
                    , content = @Content(schema = @Schema(type = "string", defaultValue = "")))
    })
    @GetMapping("/budgets")
    public ResponseEntity<ApiDataResponse<Page<BudgetResponseDto>>> getAllBudgetsForAParticularUser(
            @RequestParam(name="page" ,defaultValue = "0") int pageNo, @RequestParam(name="size" ,defaultValue = "10") int pageSize
            , @RequestParam(name = "state", required = false) String state) {
        List<SearchCriteria> searchCriterias = null;
        if (StringUtils.isNotEmpty(state)) {
            searchCriterias = new ArrayList<>();
            searchCriterias.add(new SearchCriteria("state", state));
        }
        Page<BudgetResponseDto> budgetResponseDtos = budgetService.getBudgets(pageSize, pageNo, searchCriterias);
        return ApiResponseUtil.response(HttpStatus.OK, budgetResponseDtos, "Successful");
    }


    private void validateRequest(CreateBudgetRequestDTO createBudgetRequest, BudgetPeriodHandler budgetPeriodHandler) {
        budgetPeriodHandler.validateRequest(createBudgetRequest);
    }


}
