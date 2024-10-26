package com.divyajyoti.user_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_EXPENSE_RECORDS")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserExpenseEntity extends BaseEntity{

    @Column(name = "AMOUNT")
    private long amount;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "GROUP_ID")
    private GroupEntity groupEntity;

}
