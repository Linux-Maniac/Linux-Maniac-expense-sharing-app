package com.divyajyoti.user_management.rests.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class GenericRestException extends RuntimeException {

    private HttpStatus httpStatus;

    public GenericRestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
