package com.decagon.decapay.dto.budget;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class BudgetCategoryResponseDto {

    private String title;

    public BudgetCategoryResponseDto(String title) {
        this.title = title;
    }
}
