package com.divyajyoti.expense_management.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EXPENSE_DETAILS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseEntity extends BaseEntity{

    private String description;

    private double amount;

    @OneToMany(mappedBy = "expenseEntity")
    private List<UserExpenseMappingEntity> userExpenseMappingEntityList;

    @ManyToOne
    @JoinColumn(name = "GROUP_ID")
    private GroupEntity groupEntity;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity userEntity;

}
