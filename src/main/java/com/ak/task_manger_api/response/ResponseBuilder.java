package com.ak.task_manger_api.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {
    public static <T> ResponseEntity<CustomResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(
                CustomResponse.<T>builder()
                        .status(HttpStatus.OK.value())
                        .message(message)
                        .data(data)
                        .build()
        );
    }

    public static <T> ResponseEntity<CustomResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        CustomResponse.<T>builder()
                                .status(HttpStatus.CREATED.value())
                                .message(message)
                                .data(data)
                                .build()
                );
    }
}
