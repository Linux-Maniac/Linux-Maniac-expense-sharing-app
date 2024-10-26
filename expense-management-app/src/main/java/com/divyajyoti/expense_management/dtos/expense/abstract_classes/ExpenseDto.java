package com.divyajyoti.expense_management.dtos.expense.abstract_classes;

import com.divyajyoti.expense_management.dtos.UserDto;
import com.divyajyoti.expense_management.dtos.split.abstract_classes.SplitDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ExpenseDto implements Serializable {

    private String name;

    private List<SplitDto> splitsList;

    private double amount;

    private int groupId;

    private UserDto paidByUser;

    private boolean isSettled;

    public abstract boolean validate();

};
