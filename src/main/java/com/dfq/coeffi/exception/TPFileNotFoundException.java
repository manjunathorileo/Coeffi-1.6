package com.dfq.coeffi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TPFileNotFoundException extends RuntimeException {

    public TPFileNotFoundException(String message) {
        super(message);
    }

    public TPFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
