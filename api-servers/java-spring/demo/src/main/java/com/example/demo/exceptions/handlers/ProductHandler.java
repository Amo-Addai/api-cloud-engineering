package com.example.demo.exceptions.handlers;

// TODO: error: cannot find symbol
// import com.example.demo.exceptions.ProductNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProductHandler {

    // TODO: fix import - ProductNotFoundException

    /*
    @ExceptionHandler(value=ProductNotFoundException.class)
    public ResponseEntity<Object> exception(
            ProductNotFoundException ex
    ) {
        System.out.println(
                "Handling exception: "
                + ex.toString()
        );
        ex.printStackTrace();

        return new ResponseEntity<>(
                "Product not found",
                HttpStatus.NOT_FOUND
        );
    }
    */

}
