package com.divyajyoti.group_management.models;

import com.divyajyoti.group_management.dtos.UserDto;
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

    private List<UserDto> members;

    private String createdByContact;

}
