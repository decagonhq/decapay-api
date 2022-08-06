package com.decagon.decapay.controller.budget;


import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.ViewBudgetDto;
import com.decagon.decapay.service.budget.BudgetService;
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
@Tag(name ="Budget Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class BudgetController {

    private final BudgetService budgetService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = USER_SUCCESSFULLY_REGISTERED),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST),
            @ApiResponse(responseCode = "409", description = USER_EMAIL_ALREADY_EXISTS) })
    @Operation(summary = "View Budget", description = "View Budget Details")
    @GetMapping("/budgets/{budgetId}")
    public ResponseEntity<ApiDataResponse<ViewBudgetDto>> fetchBudgetDetails(@PathVariable Long budgetId) {
        ViewBudgetDto budgetDto = this.budgetService.viewBudgetDetails(budgetId);
        return ApiResponseUtil.response(HttpStatus.OK, budgetDto);
        }
    }
