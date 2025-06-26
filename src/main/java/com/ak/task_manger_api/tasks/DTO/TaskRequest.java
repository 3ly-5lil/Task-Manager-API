package com.ak.task_manger_api.tasks.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class TaskRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private Boolean completed;
}
