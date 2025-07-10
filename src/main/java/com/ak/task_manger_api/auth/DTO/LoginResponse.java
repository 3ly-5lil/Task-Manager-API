package com.ak.task_manger_api.auth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body after successful login")
public record LoginResponse(
        @Schema(description = "User ID", example = "1")
        Long userId,
        @Schema(description = "Username of the user", example = "john_doe")
        String username,
        @Schema(description = "Role assigned to the user", example = "USER")
        String role,
        @Schema(description = "JWT authentication token")
        String token) {
}
