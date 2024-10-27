package com.divyajyoti.expense_management.aops;

import com.divyajyoti.expense_management.rests.exceptions.GenericRestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GenericRestException.class)
    public ResponseEntity<?> genericRestExceptionHandler(GenericRestException genericException, WebRequest request){
        Map<String, Object> respData = new HashMap<>();
        respData.put("message", genericException.getMessage());
        respData.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(respData, genericException.getHttpStatus());
    }
}
