package com.ak.task_manger_api.tasks.controllers;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.auth.services.AppUserService;
import com.ak.task_manger_api.response.ApiResponse;
import com.ak.task_manger_api.response.ResponseBuilder;
import com.ak.task_manger_api.tasks.DTO.TaskRequest;
import com.ak.task_manger_api.tasks.DTO.TaskResponse;
import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    @Autowired
    private final TaskService _service;
    @Autowired
    private final AppUserService appUserService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllOwnedTasks(Principal principal) {
        AppUser user = appUserService.getCurrentUser(principal);
//        return _service.getAllOwnedTasks(user).stream().map(Task::toDTO).toList();
        return ResponseBuilder.ok(_service.getAllOwnedTasks(user).stream().map(Task::toDTO).toList(),
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
