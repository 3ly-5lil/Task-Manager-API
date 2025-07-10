package com.ak.task_manger_api.exception.DTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ApiErrorResponse {
    private boolean success;
    private Class<?> type;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private List<FieldError> details;


    public static ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            Exception exception,
            String message,
            HttpServletRequest request,
            List<FieldError> details) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .error(status.getReasonPhrase())
                .type(exception.getClass())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            Exception exception,
            String message,
            HttpServletRequest request) {

        return buildResponse(status,
                exception,
                message,
                request,
                null);
    }
}
