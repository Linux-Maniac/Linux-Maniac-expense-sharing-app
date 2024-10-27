package com.divyajyoti.group_management.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStatusDto {

    private String status;

    private String message;

    private Map<String, Object> details;

}
