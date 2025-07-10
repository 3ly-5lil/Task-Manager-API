package com.ak.task_manger_api.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ResponseBuilder {
    public static <T> ResponseEntity<CustomResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(
                CustomResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .data(data)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    public static <T> ResponseEntity<CustomResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        CustomResponse.<T>builder()
                                .success(true)
                                .message(message)
                                .data(data)
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    public static ResponseEntity<CustomResponse<Void>> noContent(String message) {
        return ResponseEntity.ok(CustomResponse.<Void>builder()
                .success(true)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

}
