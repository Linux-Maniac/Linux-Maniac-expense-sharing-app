package com.divyajyoti.expense_management.dtos.split.extends_classes;

import com.divyajyoti.expense_management.dtos.UserDto;
import com.divyajyoti.expense_management.dtos.split.abstract_classes.SplitDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PercentSplitDto extends SplitDto {

    private double percent;

    public PercentSplitDto(UserDto user, double percent){
        super(user);
        this.percent = percent;
    }

}
