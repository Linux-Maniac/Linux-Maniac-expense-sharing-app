package com.divyajyoti.expense_management.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserShareMappingModel {

    private UserModel user;

    private double amount;

}
