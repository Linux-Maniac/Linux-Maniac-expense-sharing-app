package com.divyajyoti.expense_management.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EXPENSE_DETAILS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseEntity extends BaseEntity{

    @Column(name = "EXPENSE_DESCRIPTION")
    private String description;

    @Column(name = "TOTAL_AMOUNT")
    private double totalAmount;

    @OneToMany(mappedBy = "expenseEntity", cascade = CascadeType.PERSIST)
    private List<UserExpenseMappingEntity> userExpenseMappingEntityList;

    @ManyToOne
    @JoinColumn(name = "GROUP_ID")
    private GroupEntity groupEntity;

    @ManyToOne
    @JoinColumn(name = "PAYEE_USER_ID")
    private UserEntity paidByUserEntity;

    @Column(name = "IS_SETTLED", nullable = false)
    private String isSettled = "FALSE"; // Default value as a string

    // Method to get the boolean representation
    public Boolean getIsSettledAsBoolean() {
        return "TRUE".equalsIgnoreCase(isSettled);
    }

    // Method to set the boolean value
    public void setIsSettledFromBoolean(Boolean settled) {
        this.isSettled = settled != null && settled ? "TRUE" : "FALSE";
    }

}
