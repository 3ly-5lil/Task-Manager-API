package com.ak.task_manger_api.exception.DTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiErrorResponse {
    private int status;
    private Class<?> type;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public static ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            Exception exception,
            String message,
            HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .type(exception.getClass())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
