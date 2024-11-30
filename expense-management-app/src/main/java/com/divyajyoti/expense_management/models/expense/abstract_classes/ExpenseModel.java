package com.divyajyoti.expense_management.models.expense.abstract_classes;

import com.divyajyoti.expense_management.constants.SplitType;
import com.divyajyoti.expense_management.models.GroupModel;
import com.divyajyoti.expense_management.models.UserModel;
import com.divyajyoti.expense_management.models.split.abstract_classes.SplitModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ExpenseModel implements Serializable {

    private BigInteger id;

    private String description;

    private Double totalAmount;

    private SplitType splitType;

    private GroupModel group;

    private List<SplitModel> splitDetails;

    private UserModel paidBy;

    private Boolean isSettled;

    public ExpenseModel(String description, double amount, UserModel paidBy, List<SplitModel> splitDetails) {
        this.description = description;
        this.totalAmount = amount;
        this.paidBy = paidBy;
        this.splitDetails = splitDetails;
    }

    @JsonIgnore
    public abstract boolean isValid();

}
