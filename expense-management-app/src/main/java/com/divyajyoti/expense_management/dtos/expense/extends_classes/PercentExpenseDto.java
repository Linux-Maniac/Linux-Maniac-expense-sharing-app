package com.divyajyoti.expense_management.dtos.expense.extends_classes;

import com.divyajyoti.expense_management.dtos.expense.abstract_classes.ExpenseDto;
import com.divyajyoti.expense_management.dtos.split.abstract_classes.SplitDto;
import com.divyajyoti.expense_management.dtos.split.extends_classes.PercentSplitDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PercentExpenseDto extends ExpenseDto {

    @Override
    public boolean validate() {
        for (SplitDto split : getSplitsList()) {
            if (!(split instanceof PercentSplitDto)) {
                return false;
            }
        }
        double totalPercent = 100;
        double sumSplitPercent = 0;
        for (SplitDto split : getSplitsList()) {
            PercentSplitDto exactSplit = (PercentSplitDto) split;
            sumSplitPercent += exactSplit.getPercent();
        }
        return totalPercent == sumSplitPercent;
    }

}
