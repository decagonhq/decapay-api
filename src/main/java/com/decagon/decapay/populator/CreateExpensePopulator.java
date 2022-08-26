package com.decagon.decapay.populator;

import com.decagon.decapay.constants.DateDisplayConstants;
import com.decagon.decapay.dto.budget.ExpenseDto;
import com.decagon.decapay.exception.ConversionRuntimeException;
import com.decagon.decapay.model.budget.Expenses;
import com.decagon.decapay.utils.CustomDateUtil;

public class CreateExpensePopulator extends AbstractDataPopulator<ExpenseDto, Expenses>{
    @Override
    public Expenses populate(ExpenseDto source, Expenses target) throws ConversionRuntimeException {
        target.setAmount(source.getAmount());
        target.setDescription(source.getDescription());
        target.setTransactionDate(CustomDateUtil.formatStringToLocalDate(source.getTransactionDate(), DateDisplayConstants.DATE_INPUT_FORMAT));
        return target;
    }

    @Override
    protected Expenses createTarget() {
        return null;
    }
}
