package com.ak.task_manger_api.auth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body after successful registration")
public record RegisterResponse(
        @Schema(description = "User ID of the newly registered user", example = "1")
        Long userId,
        @Schema(description = "Username of the newly registered user", example = "john_doe")
        String username,
        @Schema(description = "Role assigned to the user", example = "USER")
        String role) {
}
