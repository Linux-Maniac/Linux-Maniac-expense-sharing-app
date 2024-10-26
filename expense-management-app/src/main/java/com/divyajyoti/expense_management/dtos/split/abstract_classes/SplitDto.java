package com.divyajyoti.expense_management.dtos.split.abstract_classes;

import com.divyajyoti.expense_management.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class SplitDto {

    private UserDto user;

    private double amount;

    public SplitDto(UserDto user){
        this.user = user;
    }

}
