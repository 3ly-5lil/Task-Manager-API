package com.ak.task_manger_api.tasks.controllers;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.auth.services.AppUserService;
import com.ak.task_manger_api.tasks.DTO.TaskResponse;
import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController @RequestMapping("/tasks") @RequiredArgsConstructor
public class TaskController {
    @Autowired
    private final TaskService _service;
    @Autowired
    private final AppUserService appUserService;

    @GetMapping
    public List<TaskResponse> getAllOwnedTasks(Principal principal) {
        AppUser user = appUserService.getCurrentUser(principal);
        return _service.getAllOwnedTasks(user).stream().map(Task::toDTO).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable int id, Principal principal) {
        AppUser user = appUserService.getCurrentUser(principal);

        return ResponseEntity.ok(_service.getTaskById(id, user).toDTO());
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody Task task, Principal principal) {
        AppUser user = appUserService.getCurrentUser(principal);

        return ResponseEntity.ok(_service.createTask(task, user).toDTO());
    }

    @PatchMapping("/{id}")
    public  ResponseEntity<?> update(@PathVariable long id, @RequestBody Task task, Principal principal) {
        AppUser user = appUserService.getCurrentUser(principal);

        try {
            return ResponseEntity.ok(_service.updateTask(id, task, user).toDTO());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  delete(@PathVariable int id, Principal principal) {
        AppUser user = appUserService.getCurrentUser(principal);
        _service.deleteTask(id, user);
        return  ResponseEntity.noContent().build();
    }
}
