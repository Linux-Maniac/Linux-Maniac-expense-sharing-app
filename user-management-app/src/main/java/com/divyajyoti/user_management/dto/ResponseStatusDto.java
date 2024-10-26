package com.divyajyoti.user_management.dto;

import com.divyajyoti.user_management.entity.UserEntity;
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
