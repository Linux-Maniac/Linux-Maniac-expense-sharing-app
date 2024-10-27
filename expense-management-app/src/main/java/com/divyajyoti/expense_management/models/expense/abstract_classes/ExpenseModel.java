package com.divyajyoti.expense_management.models.expense.abstract_classes;

import com.divyajyoti.expense_management.dtos.UserDto;
import com.divyajyoti.expense_management.entities.GroupEntity;
import com.divyajyoti.expense_management.entities.UserEntity;
import com.divyajyoti.expense_management.entities.UserExpenseMappingEntity;
import com.divyajyoti.expense_management.models.split.abstract_classes.SplitModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ExpenseModel implements Serializable {

    private String description;

    private double amount;

    private List<UserExpenseMappingEntity> userExpenseMappingEntityList;

    private GroupEntity groupEntity;

    private UserEntity userEntity;

    private List<SplitModel> splitModelList;

    private UserDto paidBy;

    public ExpenseModel(String description, double amount, UserDto paidBy, List<SplitModel> splitModelList) {
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.splitModelList = splitModelList;
    }

    public abstract boolean isValid();

};
