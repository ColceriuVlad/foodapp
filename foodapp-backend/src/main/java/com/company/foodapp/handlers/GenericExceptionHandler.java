package com.company.foodapp.handlers;

import com.company.foodapp.exceptions.NotAuthorizedException;
import com.company.foodapp.models.ErrorResponse;
import com.company.foodapp.utils.DateUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GenericExceptionHandler {
    private Logger logger;
    private DateUtils dateUtils;

    @Autowired
    public GenericExceptionHandler(Logger logger, DateUtils dateUtils) {
        this.logger = logger;
        this.dateUtils = dateUtils;
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException nullPointerException) {
        var errorMessage = nullPointerException.getMessage();

        logger.info(errorMessage);
        var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage, dateUtils.getCurrentDate());
        return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthorizedException(NotAuthorizedException notAuthorizedException) {
        var errorMessage = notAuthorizedException.getMessage();

        logger.info(errorMessage);
        var errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), errorMessage, dateUtils.getCurrentDate());
        return new ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
