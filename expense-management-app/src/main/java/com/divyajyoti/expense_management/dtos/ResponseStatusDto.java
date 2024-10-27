package com.divyajyoti.expense_management.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStatusDto implements Serializable {

    private String status;

    private Object details;

}
