package com.ak.task_manger_api.tasks.controllers;

import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private final TaskService _service;

    public TaskController(TaskService service) {
        _service = service;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return _service.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable int id) {
        return _service.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Task save(@RequestBody Task task) {
        return _service.createTask(task);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<?> update(@PathVariable int id, @RequestBody Task task) {
        try {
            return ResponseEntity.ok(_service.updateTask(id, task));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  delete(@PathVariable int id) {
        _service.deleteTask(id);
        return  ResponseEntity.noContent().build();
    }
}
