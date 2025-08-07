package com.example.SpringBloggerAPI.exception.handler;

import com.example.SpringBloggerAPI.exception.types.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgument(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse.FieldValidationError(
                        error.getField(),
                        Optional.ofNullable(error.getDefaultMessage()).orElse("Validation failed")
                ))
                .toList();

        System.out.println("Taking care of validation errors");
        System.out.println(errors);

        ErrorResponse response = new ErrorResponse("Validation failed", HttpStatus.BAD_REQUEST.value(), errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPostId(PostNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCommentId(CommentNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage(), 400);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public  ResponseEntity<ErrorResponse> handlePermissionDenied(PermissionDeniedException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConflictException.class)
    public  ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ex.printStackTrace();
        ErrorResponse response = new ErrorResponse("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
