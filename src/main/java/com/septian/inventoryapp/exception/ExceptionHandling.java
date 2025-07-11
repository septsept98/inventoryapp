package com.septian.inventoryapp.exception;

import com.septian.inventoryapp.model.dto.ErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandling extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<Map<String, String>> handlerErrorException(ErrorException ex){
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("errorReason", ex.getErrorReason());
        return new ResponseEntity<>(body, ex.getStatus());
    }
}
