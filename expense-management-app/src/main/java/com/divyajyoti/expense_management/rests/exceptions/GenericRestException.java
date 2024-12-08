package com.divyajyoti.expense_management.rests.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper=false)
public class GenericRestException extends RuntimeException{

    private HttpStatus httpStatus;

    public GenericRestException(String message, HttpStatus httpStatus){
        super(message);
        this.httpStatus = httpStatus;
    }

}
