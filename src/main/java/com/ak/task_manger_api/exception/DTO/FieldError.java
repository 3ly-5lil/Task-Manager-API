package com.ak.task_manger_api.exception.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Details about a specific validation error")
public class FieldError {
    @Schema(description = "The name of the field that failed validation", example = "username")
    private String field;
    @Schema(description = "The validation error message", example = "Username must be between 3 and 20 characters")
    private String error;
}
