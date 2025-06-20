package com.ak.task_manger_api.tasks.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private Boolean completed;
}
