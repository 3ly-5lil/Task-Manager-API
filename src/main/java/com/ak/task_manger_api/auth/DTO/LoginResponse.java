package com.ak.task_manger_api.auth.DTO;

public record LoginResponse(Long userId, String username, String role, String token) {
}
