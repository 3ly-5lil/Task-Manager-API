package com.ak.task_manger_api.tasks.services;

import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private final TaskRepository _repository;

    public TaskService(TaskRepository repository) {
        _repository = repository;
    }

    public List<Task> getAllTasks() {
        return _repository.findAll();
    }

    public Optional<Task> getTaskById(long id) {
        return _repository.findById(id);
    }

    public Task createTask(Task task) {
        return _repository.save(task);
    }

    public Task updateTask(long id, Task updatedTask) throws RuntimeException {
        return _repository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setCompleted(updatedTask.isCompleted());
            return _repository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public void deleteTask(long id) {
        _repository.deleteById(id);
    }
}
