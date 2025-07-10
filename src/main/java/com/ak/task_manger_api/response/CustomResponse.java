package com.ak.task_manger_api.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomResponse<T> {
    private int status;
    private String message;
    private T data;
}
