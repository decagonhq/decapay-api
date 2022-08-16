package com.decagon.decapay.model.budget;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetLineItemId implements Serializable {

    @Column(name = "budget_id")
    long budgetId;

    @Column(name = "budget_category_id")
    long budgetCategoryId;

}
