package com.decagon.decapay.model.budget;

import com.decagon.decapay.constants.DateConstants;
import com.decagon.decapay.constants.SchemaConstants;
import com.decagon.decapay.model.audit.AuditListener;
import com.decagon.decapay.model.audit.AuditSection;
import com.decagon.decapay.model.audit.Auditable;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.utils.CustomDateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.decagon.decapay.constants.SchemaConstants.TABLE_BUDGET;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditListener.class)
@Table(name = TABLE_BUDGET)
public class Budget implements Auditable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = SchemaConstants.BUDGET_TITLE_SIZE)
    private String title;

    @Column(columnDefinition = "decimal(10,2) default (0)")
    private BigDecimal totalAmountSpentSoFar;

    @Column(columnDefinition = "decimal(10,2) default (0)")
    private BigDecimal projectedAmount;

    private LocalDate budgetStartDate;

    private LocalDate budgetEndDate;

    @Column(length = 100)
    private String notificationThreshold;

    @Column(length = SchemaConstants.BUDGET_DESC_SIZE)
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_budget_id")
    private Budget parentBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetPeriod budgetPeriod;

    @OneToMany(mappedBy = "budget", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, orphanRemoval = true)
    private Set<BudgetLineItem> budgetLineItems = new HashSet<>();

    @Embedded
    private AuditSection auditSection = new AuditSection();

    public void addBudgetLineItem(BudgetCategory budgetCategory,BigDecimal projectedAmount) {
        BudgetLineItem budgetLineItem = new BudgetLineItem(this, budgetCategory, projectedAmount);
        budgetLineItem.setId(new BudgetLineItemId(this.getId(), budgetCategory.getId()));
        this.budgetLineItems.add(budgetLineItem);
    }

    public void removeBudgetLineItem(BudgetCategory budgetCategory) {
        BudgetLineItem budgetLineItem = getBudgetLineItem(budgetCategory);
        this.budgetLineItems.remove(budgetLineItem);
        budgetCategory.removeBudgetLineItem(budgetLineItem);
        budgetLineItem.setBudget(null);
        budgetLineItem.setBudgetCategory(null);
    }

    public BudgetLineItem getBudgetLineItem(BudgetCategory category) {
        return this.budgetLineItems
                .stream()
                .filter(lineItem -> lineItem.getBudgetCategory().getId().equals(category.getId()))
                .findFirst()
                .orElse(null);
    }

    public BigDecimal calculatePercentageAmountSpent(){
        if(this.getTotalAmountSpentSoFar() == null||this.projectedAmount==null||this.projectedAmount.equals(BigDecimal.valueOf(0.0))){
            return BigDecimal.ZERO.setScale(1, RoundingMode.CEILING);
        }
        BigDecimal spentSoFar = this.getTotalAmountSpentSoFar().divide(this.getProjectedAmount(), BigDecimal.ROUND_HALF_DOWN);
        return spentSoFar.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.CEILING);
    }

    public BigDecimal calculateBudgetLineItemsTotalAmount(){
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (BudgetLineItem budgetLineItem : this.getBudgetLineItems()){
            totalAmount = totalAmount.add(budgetLineItem.getProjectedAmount());
        }
        return totalAmount;
    }

    /**
     * check if expense log date is valid. A valid expense date is a date
     * within the budget(that expense is being logged for) start date and the current date. An expense must not be logged outside
     * the budget's date range(start and end date) and also for any future date(date past the current date).
     *
     * @param transactionDate
     * @return true if transaction date is valid or false otherwise
     */
    public boolean isValidExpenseTransactionDate(String transactionDate) {
        LocalDate transactionLocalDate = CustomDateUtil.formatStringToLocalDate(transactionDate, DateConstants.DATE_INPUT_FORMAT);
       LocalDate budgetValidTransactionEndDate=null;
       LocalDate now=LocalDate.now();
       if(now.isBefore(this.budgetEndDate)){
           budgetValidTransactionEndDate=now;
       }else{
           budgetValidTransactionEndDate=this.budgetEndDate;
       }
        return !(transactionLocalDate.isBefore(this.getBudgetStartDate()) || transactionLocalDate.isAfter(budgetValidTransactionEndDate));
    }
}
