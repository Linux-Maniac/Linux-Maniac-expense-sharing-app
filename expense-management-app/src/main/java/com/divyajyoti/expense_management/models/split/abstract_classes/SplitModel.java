package com.divyajyoti.expense_management.models.split.abstract_classes;

import com.divyajyoti.expense_management.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class SplitModel {

    private UserDto user;

    private double amount;

    public SplitModel(UserDto user){
        this.user = user;
    }

}
