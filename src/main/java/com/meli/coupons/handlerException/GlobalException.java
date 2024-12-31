package com.meli.coupons.handlerException;

import com.meli.coupons.exception.BusinessException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(exception = {Exception.class})
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("Error in API "+ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
