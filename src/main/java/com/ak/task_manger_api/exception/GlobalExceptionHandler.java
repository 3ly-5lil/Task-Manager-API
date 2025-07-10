package com.ak.task_manger_api.exception;

import com.ak.task_manger_api.exception.DTO.ApiErrorResponse;
import com.ak.task_manger_api.exception.DTO.FieldError;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.ak.task_manger_api.exception.DTO.ApiErrorResponse.buildResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityExistsException.class)
    ResponseEntity<ApiErrorResponse> handleEntityExistsException(EntityExistsException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, exception, exception.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, exception, "Invalid username or password", request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception, exception.getMessage(), request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(EntityNotFoundException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception, exception.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, exception, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(field -> new FieldError(field.getField(), field.getDefaultMessage()))
                .toList();

        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, exception, "Validation failed", request, fieldErrors);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception, HttpServletRequest request) {
        logger.error("Unexpected error", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception, "Unexpected error", request);
    }
}
