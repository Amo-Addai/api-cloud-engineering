package com.example.demo.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProductHandler {

    @ExceptionHandler(value=ProductNotFoundException.class)
    public ResponseEntity<Object> exception(
            ProductNotFoundException ex
    ) {
        return null;
    }

}

// ProductNotFoundException can be throw'n in any controller route-handlers
class ProductNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}