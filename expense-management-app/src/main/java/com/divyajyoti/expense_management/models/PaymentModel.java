package com.divyajyoti.expense_management.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentModel {

    private UserModel fromUser;

    private UserModel toUser;

    private Double amount;

}
