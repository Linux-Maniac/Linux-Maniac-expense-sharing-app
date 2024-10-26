package com.divyajyoti.group_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {

    private String name;

    private List<UserDto> members;

    private String createdByContact;

}
