package com.divyajyoti.expense_management.models.expense.extends_classes;

import com.divyajyoti.expense_management.constants.SplitType;
import com.divyajyoti.expense_management.models.expense.abstract_classes.ExpenseModel;
import com.divyajyoti.expense_management.models.split.UserModel;
import com.divyajyoti.expense_management.models.split.abstract_classes.SplitModel;
import com.divyajyoti.expense_management.models.split.extends_classes.PercentSplitModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PercentExpenseModel extends ExpenseModel {

    public PercentExpenseModel(String description, double totalAmount
            , UserModel paidBy, List<SplitModel> splitModelList) {
        super(description, totalAmount, paidBy, splitModelList);
        setSplitType(SplitType.PERCENT);
    }

    @Override
    public boolean isValid() {
        for (SplitModel split : getSplitDetails()) {
            if (!(split instanceof PercentSplitModel)) {
                return false;
            }
        }
        double totalPercent = 100;
        double sumSplitPercent = 0;
        for (SplitModel split : getSplitDetails()) {
            PercentSplitModel exactSplit = (PercentSplitModel) split;
            sumSplitPercent += exactSplit.getPercent();
        }
        return totalPercent == sumSplitPercent;
    }

}
