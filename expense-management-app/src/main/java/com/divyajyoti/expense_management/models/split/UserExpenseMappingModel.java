package com.divyajyoti.expense_management.models.split;

import com.divyajyoti.expense_management.constants.ExpenseType;
import com.divyajyoti.expense_management.models.expense.abstract_classes.ExpenseModel;

public class UserExpenseMappingModel {

    private ExpenseModel expense;

    private UserModel user;

    private double amount;

    private ExpenseType expenseType;

}
