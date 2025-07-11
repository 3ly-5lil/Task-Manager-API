package com.ak.task_manger_api.tasks.controllers;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.auth.services.AppUserService;
import com.ak.task_manger_api.response.CustomResponse;
import com.ak.task_manger_api.response.PaginatedResponse;
import com.ak.task_manger_api.response.ResponseBuilder;
import com.ak.task_manger_api.tasks.DTO.TaskRequest;
import com.ak.task_manger_api.tasks.DTO.TaskResponse;
import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tasks", description = "Manage your personal tasks")
public class TaskController {

    @Autowired
    private final TaskService _service;
    @Autowired
    private final AppUserService appUserService;

    @Operation(summary = "Get all tasks", description = "Returns paginated tasks owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid")
    })
    @GetMapping
    public ResponseEntity<CustomResponse<PaginatedResponse<TaskResponse>>> getAllOwnedTasks(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of tasks per page") @RequestParam(defaultValue = "10") int size,
            Principal principal
    ) {
        AppUser user = appUserService.getCurrentUser(principal);
        Page<TaskResponse> tasksPage = _service.getAllOwnedTasks(user, page, size).map(Task::toDTO);
        PaginatedResponse<TaskResponse> paginatedResponse = new PaginatedResponse<>(tasksPage);
        return ResponseBuilder.ok(paginatedResponse, "Tasks fetched successfully");
    }

    @Operation(summary = "Get task by ID", description = "Retrieve a single task by its ID for the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<TaskResponse>> getTaskById(
            @Parameter(description = "Task ID") @PathVariable int id,
            Principal principal
    ) {
        AppUser user = appUserService.getCurrentUser(principal);
        return ResponseBuilder.ok(_service.getTaskById(id, user).toDTO(), "Task fetched successfully");
    }

    @Operation(summary = "Create new task", description = "Create a new task for the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid")
    })
    @PostMapping
    public ResponseEntity<CustomResponse<TaskResponse>> createTask(
            @Parameter(description = "Task request body") @RequestBody @Valid TaskRequest task,
            Principal principal
    ) {
        AppUser user = appUserService.getCurrentUser(principal);
        return ResponseBuilder.created(_service.createTask(task, user).toDTO(), "Task created successfully");
    }

    @Operation(summary = "Update task", description = "Update an existing task for the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<CustomResponse<TaskResponse>> update(
            @Parameter(description = "Task ID") @PathVariable long id,
            @Parameter(description = "Updated task request body") @RequestBody @Valid TaskRequest task,
            Principal principal
    ) {
        AppUser user = appUserService.getCurrentUser(principal);
        return ResponseBuilder.ok(_service.updateTask(id, task, user).toDTO(), "Task updated successfully");
    }

    @Operation(summary = "Delete task", description = "Delete a task by ID for the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Void>> delete(
            @Parameter(description = "Task ID") @PathVariable int id,
            Principal principal
    ) {
        AppUser user = appUserService.getCurrentUser(principal);
        _service.deleteTask(id, user);
        return ResponseBuilder.noContent("Task deleted successfully");
    }

    @Operation(summary = "Restore task", description = "Restore a deleted task for the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task restored successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid")
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<CustomResponse<TaskResponse>> restore(
            @Parameter(description = "Task ID") @PathVariable int id,
            Principal principal
    ) {
        AppUser user = appUserService.getCurrentUser(principal);
        Task restoredTask = _service.restoreTask(id, user);
        return ResponseBuilder.ok(restoredTask.toDTO(), "Task restored successfully");
    }
}
