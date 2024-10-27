package com.divyajyoti.expense_management.entities;

import com.divyajyoti.expense_management.constants.ExpenseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "USER_EXPENSE_RECORDS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserExpenseMappingEntity extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "EXPENSE_ID")
    private ExpenseEntity expenseEntity;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity userEntity;

    @Column(name = "AMOUNT")
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "EXPENSE_TYPE")
    private ExpenseType expenseType;

}
