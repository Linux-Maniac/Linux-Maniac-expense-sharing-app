package com.divyajyoti.expense_management.dtos;

import com.divyajyoti.expense_management.constants.ExpenseType;
import com.divyajyoti.expense_management.constants.SplitType;
import com.divyajyoti.expense_management.models.split.extends_classes.GenericSplitModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto implements Serializable {

    private String description;

    private SplitType splitType;

    private List<GenericSplitModel> userSplitsList;

    private double totalAmount;

    private UserDto paidBy;

    private BigInteger groupId;

}
