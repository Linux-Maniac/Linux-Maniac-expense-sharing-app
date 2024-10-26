package com.divyajyoti.expense_management.dtos.expense.extends_classes;

import com.divyajyoti.expense_management.dtos.expense.abstract_classes.ExpenseDto;
import com.divyajyoti.expense_management.dtos.split.abstract_classes.SplitDto;
import com.divyajyoti.expense_management.dtos.split.extends_classes.ExactSplitDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExactExpenseDto extends ExpenseDto {

    @Override
    public boolean validate() {
        for (SplitDto split : getSplitsList()) {
            if (!(split instanceof ExactSplitDto)) {
                return false;
            }
        }
        double totalAmount = getAmount();
        double sumSplitAmount = 0;
        for (SplitDto split : getSplitsList()) {
            ExactSplitDto exactSplit = (ExactSplitDto) split;
            sumSplitAmount += exactSplit.getAmount();
        }
        return totalAmount == sumSplitAmount;
    }

}
