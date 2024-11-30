package com.divyajyoti.expense_management.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    private BigInteger id;

    private String name;

    private String contact;

    private String email;

    private List<GroupModel> memberOfGroups;

}
