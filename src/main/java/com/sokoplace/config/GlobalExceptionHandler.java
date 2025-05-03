package com.sokoplace.config;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Data Transfer Object (DTO)
    public record ErrorResponse(String error) {}

    // Option: public ... handleAuthenticationException(AuthenticationException ex)
    // ...then add 'ex.getMessage' to errorResponse DTO

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException() {
        ErrorResponse errorResponse = new ErrorResponse("AUTHENTICATION FAILED - CHECK CREDENTIALS");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);  // 401
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException() {
        ErrorResponse errorResponse = new ErrorResponse("ACCESS DENIED");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);  // 403
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound() {
        ErrorResponse errorResponse = new ErrorResponse("RESOURCE NOT FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);  // 404
    }

    // When you save data that already exists, e.g. unique email address
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation() {
        ErrorResponse errorResponse = new ErrorResponse("UNIQUE CONSTRAINT ALREADY EXISTS");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);  // 409
    }

    // Handle '@Valid' '@RequestBody' validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException() {
        ErrorResponse errorResponse = new ErrorResponse("VALIDATION ERROR");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);  // 400
    }

    // Handle '@ModelAttribute' form data validation exceptions
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException() {
        ErrorResponse errorResponse = new ErrorResponse("FORM VALIDATION ERROR");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);  // 400
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handlePathVariableException() {
        ErrorResponse errorResponse = new ErrorResponse("MISSING PATH VARIABLE");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleRequestParameterException() {
        ErrorResponse errorResponse = new ErrorResponse("MISSING REQUEST PARAMETER");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleArgumentTypeException() {
        ErrorResponse errorResponse = new ErrorResponse("ARGUMENT TYPE MISMATCH");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> handleMissingCookieException() {
        ErrorResponse errorResponse = new ErrorResponse("MISSING REQUEST COOKIE");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMethodException() {
        ErrorResponse errorResponse = new ErrorResponse("METHOD NOT SUPPORTED");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);  // 405
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleResponseFormatNotSupported() {
        ErrorResponse errorResponse = new ErrorResponse("MEDIA FORMAT NOT SUPPORTED");
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);  // 415
    }

    // Wraps JPA and JDBC exceptions
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseExceptions() {
        ErrorResponse errorResponse = new ErrorResponse("OPERATION NOT SUCCESSFUL");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);  // 503
    }

    // General exception handler. Handles any exception not explicitly defined above
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException() {
        ErrorResponse errorResponse = new ErrorResponse("AN ERROR OCCURRED");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);  // 500
    }
}
