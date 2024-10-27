package com.divyajyoti.expense_management.models.expense.extends_classes;

import com.divyajyoti.expense_management.models.expense.abstract_classes.ExpenseModel;
import com.divyajyoti.expense_management.models.split.abstract_classes.SplitModel;
import com.divyajyoti.expense_management.models.split.extends_classes.EqualSplitModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class EqualExpenseModel extends ExpenseModel {

    @Override
    public boolean isValid() {
        for (SplitModel split : getSplitModelList()) {
            if (!(split instanceof EqualSplitModel)) {
                return false;
            }
        }
        return true;
    }

}
