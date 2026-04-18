package com.satvik.sysd.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public static final String ERROR = "error";

    @ExceptionHandler(exception = {RateLimitException.class})
    public ResponseEntity<Map<String, String>> handleClientError(Exception ex) {
        log.error("{}: ", ERROR, ex);
        Map<String, String> errorBody = new java.util.HashMap<>(Map.of(ERROR, ex.getMessage()));
        return new ResponseEntity<>(errorBody, HttpStatus.TOO_MANY_REQUESTS);
    }
}
