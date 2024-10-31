package com.divyajyoti.expense_management.models.split;

import com.divyajyoti.expense_management.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupModel {

    private BigInteger id;

    private String name;

    private List<UserEntity> members;

}
