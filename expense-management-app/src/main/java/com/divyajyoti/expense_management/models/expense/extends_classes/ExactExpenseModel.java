package com.divyajyoti.expense_management.models.expense.extends_classes;

import com.divyajyoti.expense_management.dtos.UserDto;
import com.divyajyoti.expense_management.models.expense.abstract_classes.ExpenseModel;
import com.divyajyoti.expense_management.models.split.abstract_classes.SplitModel;
import com.divyajyoti.expense_management.models.split.extends_classes.ExactSplitModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ExactExpenseModel extends ExpenseModel {

    public ExactExpenseModel(String name, double amount, UserDto paidBy, List<SplitModel> splits) {
        super(name, amount, paidBy, splits);
    }

    @Override
    public boolean isValid() {
        for (SplitModel split : getSplitModelList()) {
            if (!(split instanceof ExactSplitModel)) {
                return false;
            }
        }
        double totalAmount = getAmount();
        double sumSplitAmount = 0;
        for (SplitModel split : getSplitModelList()) {
            ExactSplitModel exactSplit = (ExactSplitModel) split;
            sumSplitAmount += exactSplit.getAmount();
        }
        return totalAmount == sumSplitAmount;
    }

}
