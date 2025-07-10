package com.ak.task_manger_api.auth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Request body for user registration")
public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be 3–20 characters long")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, digits, and underscores")
        @Schema(description = "New user's username", example = "john_doe")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Schema(description = "New user's password", example = "securePassword123")
        String password) {
}
