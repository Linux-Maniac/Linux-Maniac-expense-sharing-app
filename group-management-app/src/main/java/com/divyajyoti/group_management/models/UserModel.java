package com.divyajyoti.group_management.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    private BigInteger id;

    private String name;

    private String contact;

    private String email;

    private String createdAt;

    private String lastModifiedAt;

    private String firstName;

    private String lastName;

}
