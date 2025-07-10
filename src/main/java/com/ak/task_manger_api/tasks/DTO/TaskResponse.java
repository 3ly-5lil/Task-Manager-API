package com.ak.task_manger_api.tasks.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "Response DTO for a Task")
public class TaskResponse {
    @Schema(description = "Task ID", example = "1")
    private Long id;
    @Schema(description = "Title of the task", example = "Finish homework")
    private String title;
    @Schema(description = "Task description", example = "Complete the math assignment")
    private String description;
    @Schema(description = "Completion status", example = "false")
    private Boolean completed;
    @Schema(description = "The user id associated with the task", example = "false")
    private Long userId;
}
