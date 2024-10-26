package com.divyajyoti.expense_management.dtos.split.extends_classes;

import com.divyajyoti.expense_management.dtos.UserDto;
import com.divyajyoti.expense_management.dtos.split.abstract_classes.SplitDto;

public class ExactSplitDto extends SplitDto {

    public ExactSplitDto(UserDto user, double amount){
        super(user);
        this.setAmount(amount);
    }

}
