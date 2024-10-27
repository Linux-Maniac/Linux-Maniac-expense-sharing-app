package com.divyajyoti.expense_management.models.split.extends_classes;

import com.divyajyoti.expense_management.dtos.UserDto;
import com.divyajyoti.expense_management.models.split.abstract_classes.SplitModel;

public class ExactSplitModel extends SplitModel {

    public ExactSplitModel(UserDto user, double amount){
        super(user);
        this.setAmount(amount);
    }

}
