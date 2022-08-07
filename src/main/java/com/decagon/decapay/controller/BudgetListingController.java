package com.decagon.decapay.controller;


import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.dto.BudgetResponseDto;
import com.decagon.decapay.dto.SearchCriteria;
import com.decagon.decapay.service.BudgetListService;
import com.decagon.decapay.utils.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.decagon.decapay.constants.ResponseMessageConstants.*;


@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.basepath-api}")
public class BudgetListingController {

    private final BudgetListService budgetListService;



    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = RETURN_BUDGET_LISTS_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = INVALID_REQUEST),
            @ApiResponse(responseCode = "404", description = NOT_FOUND)})
    @Operation(summary = "Returns user's budget list successfully")
    @GetMapping("/budgets")
    public ResponseEntity<ApiDataResponse<Page<BudgetResponseDto>>> getAllBudgetsForAParticularUser(
                                                                                              @RequestParam(defaultValue = "0") int pageNo,
                                                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                                                              @RequestParam(name = "state", required = false) String state
                                                                                              ){

        List<SearchCriteria> searchCriterias = null;
        if (StringUtils.isNotEmpty(state)){
            searchCriterias = new ArrayList<>();
            searchCriterias.add(new SearchCriteria("state", state));
        }
        Page<BudgetResponseDto> budgetResponseDtos = budgetListService.getBudgets(pageSize,pageNo, searchCriterias);
        return ApiResponseUtil.response(HttpStatus.OK, budgetResponseDtos, "Successful");
    }
}
