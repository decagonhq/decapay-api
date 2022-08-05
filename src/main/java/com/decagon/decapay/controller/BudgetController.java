package com.decagon.decapay.controller;

import static com.decagon.decapay.constants.ResponseMessageConstants.BUDGET_SUCCESSFULLY_CREATED;
import static com.decagon.decapay.constants.ResponseMessageConstants.INVALID_REQUEST;
import static com.decagon.decapay.constants.ResponseMessageConstants.REQUEST_FORBIDDEN;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.CreateBudgetRequestDTO;
import com.decagon.decapay.dto.CreateBudgetResponseDTO;
import com.decagon.decapay.service.BudgetService;
import com.decagon.decapay.utils.ApiResponseUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;


@SecurityRequirements
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class BudgetController {
	private final BudgetService budgetService;

	public BudgetController(final BudgetService budgetService) {
		this.budgetService = budgetService;
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = BUDGET_SUCCESSFULLY_CREATED),
		@ApiResponse(responseCode = "400", description = INVALID_REQUEST),
		@ApiResponse(responseCode = "403", description = REQUEST_FORBIDDEN) })
	@Operation(summary = "Create budget", description = "Create new user budget for with all mandatory fields.")
	@PostMapping("/budgets")
	public ResponseEntity<ApiDataResponse<CreateBudgetResponseDTO>> createBudget(@Valid @RequestBody CreateBudgetRequestDTO createBudgetRequest) {
		return ApiResponseUtil.response(HttpStatus.CREATED, budgetService.createBudget(createBudgetRequest),
			BUDGET_SUCCESSFULLY_CREATED);
	}
}
