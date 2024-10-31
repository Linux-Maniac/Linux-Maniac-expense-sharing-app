package com.divyajyoti.expense_management.models.split.extends_classes;

import com.divyajyoti.expense_management.dtos.UserDto;
import com.divyajyoti.expense_management.models.split.abstract_classes.SplitModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class PercentSplitModel extends SplitModel {

    private double percent;

    public PercentSplitModel(UserDto user, double percent){
        super(user);
        this.percent = percent;
    }

}
