package com.decagon.decapay.populator;

import com.decagon.decapay.constants.DateConstants;
import com.decagon.decapay.dto.budget.ExpenseDto;
import com.decagon.decapay.exception.ConversionRuntimeException;
import com.decagon.decapay.model.budget.Expense;
import com.decagon.decapay.utils.CustomDateUtil;

public class CreateExpensePopulator extends AbstractDataPopulator<ExpenseDto, Expense>{
    @Override
    public Expense populate(ExpenseDto source, Expense target) throws ConversionRuntimeException {
        target.setAmount(source.getAmount());
        target.setDescription(source.getDescription());
        target.setTransactionDate(CustomDateUtil.formatStringToLocalDate(source.getTransactionDate(), DateConstants.DATE_INPUT_FORMAT));
        return target;
    }

    @Override
    protected Expense createTarget() {
        return null;
    }
}
