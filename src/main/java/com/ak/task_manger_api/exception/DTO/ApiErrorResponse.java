package com.ak.task_manger_api.exception.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Standard error response")
public class ApiErrorResponse {
    @Schema(description = "Indicates the operation failed", example = "false")
    private boolean success;
    @Schema(description = "The exception class", example = "UsernameNotFoundException.class")
    private Class<?> type;
    @Schema(description = "HTTP status reason", example = "The exception class")
    private String error;
    @Schema(description = "Error message", example = "User not found")
    private String message;
    @Schema(description = "The requested URI", example = "/api/tasks/1")
    private String path;
    @Schema(description = "When the error occurred", example = "2025-06-20T22:30:00Z")
    private LocalDateTime timestamp;
    @Schema(description = "Field-level validation error details (optional)")
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
