package com.divyajyoti.expense_management.dtos.expense.extends_classes;

import com.divyajyoti.expense_management.dtos.expense.abstract_classes.ExpenseDto;
import com.divyajyoti.expense_management.dtos.split.abstract_classes.SplitDto;
import com.divyajyoti.expense_management.dtos.split.extends_classes.EqualSplitDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EqualExpenseDto extends ExpenseDto {

    @Override
    public boolean validate() {
        for (SplitDto split : getSplitsList()) {
            if (!(split instanceof EqualSplitDto)) {
                return false;
            }
        }
        return true;
    }

}
