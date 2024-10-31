package com.divyajyoti.expense_management.models.expense.extends_classes;

import com.divyajyoti.expense_management.constants.SplitType;
import com.divyajyoti.expense_management.models.expense.abstract_classes.ExpenseModel;
import com.divyajyoti.expense_management.models.split.UserModel;
import com.divyajyoti.expense_management.models.split.abstract_classes.SplitModel;
import com.divyajyoti.expense_management.models.split.extends_classes.EqualSplitModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class EqualExpenseModel extends ExpenseModel {

    public EqualExpenseModel(String description, double totalAmount
            , UserModel paidBy, List<SplitModel> splits) {
        super(description, totalAmount, paidBy, splits);
        setSplitType(SplitType.EQUAL);
    }

    @Override
    public boolean isValid() {
        for (SplitModel split : getSplitDetails()) {
            if (!(split instanceof EqualSplitModel)) {
                return false;
            }
        }
        return true;
    }

}
