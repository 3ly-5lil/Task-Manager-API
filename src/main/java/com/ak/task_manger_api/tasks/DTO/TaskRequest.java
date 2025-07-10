package com.ak.task_manger_api.tasks.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for creating/updating a task")
public class TaskRequest {
    @NotBlank
    @Schema(description = "Title of the task", example = "Finish homework")
    private String title;
    @NotBlank
    @Schema(description = "Task description", example = "Math exercise")
    private String description;
    @NotNull
    @Schema(description = "Completion status", example = "false")
    private Boolean completed;
}
