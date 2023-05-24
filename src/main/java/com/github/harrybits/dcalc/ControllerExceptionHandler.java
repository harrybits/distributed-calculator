package com.github.harrybits.dcalc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.UnknownHostException;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UnknownHostException.class)
    @ResponseStatus(value = HttpStatus.FAILED_DEPENDENCY)
    public String resourceNotFoundException(UnknownHostException ex, WebRequest request) {
        return ex.getMessage() + " service missing!";
    }
}

