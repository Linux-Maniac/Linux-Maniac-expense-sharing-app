package com.divyajyoti.expense_management.dtos.split.extends_classes;

import com.divyajyoti.expense_management.dtos.UserDto;
import com.divyajyoti.expense_management.dtos.split.abstract_classes.SplitDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class EqualSplitDto extends SplitDto {

    public EqualSplitDto(UserDto user){
        super(user);
    }

}
