package com.meli.coupons.controller.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalException {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException ex) {
        log.error("Business exception: "+ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(exception = {Exception.class})
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("General exception: "+ex.getMessage(),ex);
        return new ResponseEntity<>("Error in API "+ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
