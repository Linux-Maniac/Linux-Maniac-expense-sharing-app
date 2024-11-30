package com.divyajyoti.expense_management.models.expense.extends_classes;

import com.divyajyoti.expense_management.constants.SplitType;
import com.divyajyoti.expense_management.models.expense.abstract_classes.ExpenseModel;
import com.divyajyoti.expense_management.models.UserModel;
import com.divyajyoti.expense_management.models.split.abstract_classes.SplitModel;
import com.divyajyoti.expense_management.models.split.extends_classes.ExactSplitModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class ExactExpenseModel extends ExpenseModel {

    public ExactExpenseModel(String description, double amount, UserModel paidBy
            , List<SplitModel> splits) {
        super(description, amount, paidBy, splits);
        setSplitType(SplitType.EXACT);
    }

    @Override
    public boolean isValid() {
        for (SplitModel split : getSplitDetails()) {
            if (!(split instanceof ExactSplitModel)) {
                return false;
            }
        }
        double totalAmount = getTotalAmount();
        double sumSplitAmount = 0;
        for (SplitModel split : getSplitDetails()) {
            ExactSplitModel exactSplit = (ExactSplitModel) split;
            sumSplitAmount += exactSplit.getAmount();
        }
        return totalAmount == sumSplitAmount;
    }

}
