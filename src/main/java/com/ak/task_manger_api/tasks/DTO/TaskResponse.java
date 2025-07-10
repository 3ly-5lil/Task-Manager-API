package com.ak.task_manger_api.tasks.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

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
    @Schema(description = "Creation timestamp", example = "2025-06-20T12:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "Last update timestamp", example = "2025-06-21T18:30:00")
    private LocalDateTime updatedAt;
}
