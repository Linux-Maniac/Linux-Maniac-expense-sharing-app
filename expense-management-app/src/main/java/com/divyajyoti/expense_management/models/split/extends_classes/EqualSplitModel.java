package com.divyajyoti.expense_management.models.split.extends_classes;

import com.divyajyoti.expense_management.dtos.UserDto;
import com.divyajyoti.expense_management.models.split.abstract_classes.SplitModel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class EqualSplitModel extends SplitModel {

    public EqualSplitModel(UserDto user){
        super(user);
    }

}
