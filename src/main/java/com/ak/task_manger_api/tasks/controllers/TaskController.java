package com.ak.task_manger_api.tasks.controllers;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.auth.services.AppUserService;
import com.ak.task_manger_api.response.ApiResponse;
import com.ak.task_manger_api.response.PaginatedResponse;
import com.ak.task_manger_api.response.ResponseBuilder;
import com.ak.task_manger_api.tasks.DTO.TaskRequest;
import com.ak.task_manger_api.tasks.DTO.TaskResponse;
import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.services.TaskService;
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
public class TaskController {
    @Autowired
    private final TaskService _service;
    @Autowired
    private final AppUserService appUserService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<TaskResponse>>> getAllOwnedTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal
    ) {
        AppUser user = appUserService.getCurrentUser(principal);
        Page<TaskResponse> tasksPage = _service.getAllOwnedTasks(user, page, size).map(Task::toDTO);
        PaginatedResponse<TaskResponse> paginatedResponse = new PaginatedResponse<>(tasksPage);
        return ResponseBuilder.ok(paginatedResponse,
                "Tasks fetched successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(@PathVariable int id, Principal principal) {
        AppUser user = appUserService.getCurrentUser(principal);

        return ResponseBuilder.ok(_service.getTaskById(id, user).toDTO(),
                "Task fetched successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@RequestBody @Valid TaskRequest task, Principal principal) {
        AppUser user = appUserService.getCurrentUser(principal);

        return ResponseBuilder.created(_service.createTask(task, user).toDTO(),
                "Task created successfully");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> update(@PathVariable long id, @RequestBody @Valid TaskRequest task, Principal principal) {
        AppUser user = appUserService.getCurrentUser(principal);

        return ResponseBuilder.ok(_service.updateTask(id, task, user).toDTO(),
                "Task updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable int id, Principal principal) {
        AppUser user = appUserService.getCurrentUser(principal);
        _service.deleteTask(id, user);
        return ResponseBuilder.ok(null, "Task deleted successfully");
    }
}
