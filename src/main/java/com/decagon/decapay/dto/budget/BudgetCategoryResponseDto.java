package com.decagon.decapay.dto.budget;



import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BudgetCategoryResponseDto {

    private Long id;
    private String title;

    public BudgetCategoryResponseDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
