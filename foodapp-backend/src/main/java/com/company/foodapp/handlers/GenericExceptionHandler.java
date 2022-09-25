package com.company.foodapp.handlers;

import com.company.foodapp.exceptions.*;
import com.company.foodapp.models.ResponseObject;
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
    public ResponseEntity<ResponseObject> handleNullPointerException(NullPointerException nullPointerException) {
        var errorMessage = nullPointerException.getMessage();

        logger.info(errorMessage);
        var responseObject = new ResponseObject(HttpStatus.NOT_FOUND.value(), errorMessage, dateUtils.getCurrentDate());
        return new ResponseEntity(responseObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ResponseObject> handleNotAuthenticatedException(NotAuthenticatedException notAuthenticatedException) {
        var errorMessage = notAuthenticatedException.getMessage();
        logger.info(errorMessage);

        var responseObject = new ResponseObject(HttpStatus.FORBIDDEN.value(), errorMessage, dateUtils.getCurrentDate());
        return new ResponseEntity<>(responseObject, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ResponseObject> handleInvalidOperationException(InvalidOperationException invalidOperationException) {
        var errorMessage = invalidOperationException.getMessage();
        logger.info(errorMessage);

        var responseObject = new ResponseObject(HttpStatus.BAD_REQUEST.value(), errorMessage, dateUtils.getCurrentDate());
        return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ResponseObject> handleNotAuthorizedException(NotAuthorizedException notAuthorizedException) {
        var errorMessage = notAuthorizedException.getMessage();

        logger.info(errorMessage);
        var responseObject = new ResponseObject(HttpStatus.UNAUTHORIZED.value(), errorMessage, dateUtils.getCurrentDate());
        return new ResponseEntity(responseObject, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseObject> handleEntityNotFoundException(EntityNotFoundException entityNotFoundException) {
        var errorMessage = entityNotFoundException.getMessage();
        logger.info(errorMessage);

        var responseObject = new ResponseObject(HttpStatus.NOT_FOUND.value(), errorMessage, dateUtils.getCurrentDate());
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ResponseObject> handleNotValidatedException(NotValidatedException notValidatedException) {
        var errorMessage = notValidatedException.getMessage();
        logger.info(errorMessage);

        var responseObject = new ResponseObject(HttpStatus.BAD_REQUEST.value(), errorMessage, dateUtils.getCurrentDate());
        return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject> handleException(Exception exception) {
        var errorMessage = exception.getMessage();

        logger.info(errorMessage);
        var responseObject = new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage, dateUtils.getCurrentDate());
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
