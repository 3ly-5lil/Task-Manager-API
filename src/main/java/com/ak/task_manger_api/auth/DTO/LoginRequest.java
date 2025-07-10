package com.ak.task_manger_api.auth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for user login")
public record LoginRequest(
        @Schema(description = "Username of the user", example = "john_doe")
        String username,
        @Schema(description = "Password of the user", example = "password123")
        String password) {
}
