package com.divyajyoti.group_management.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponseDto {

    private BigInteger id;

    private String name;

    private List<UserDto> members;

    private String createdByContact;

}
