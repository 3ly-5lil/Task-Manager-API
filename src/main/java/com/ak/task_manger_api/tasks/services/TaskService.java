package com.ak.task_manger_api.tasks.services;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class TaskService {
    @Autowired
    private final TaskRepository _repository;

    public List<Task> getAllOwnedTasks(AppUser requester) {
        return _repository.findByUserId(requester.getId());
    }

    public Task getTaskById(long id, AppUser requester) {
        Task task = _repository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (!task.getUser().getId().equals(requester.getId()))
            throw new AccessDeniedException("Access denied");

        return task;
    }

    public Task createTask(Task task, AppUser requester) {
        task.setUser(requester);
        return _repository.save(task);
    }

    public Task updateTask(long id, Task updatedTask, AppUser requester) throws RuntimeException {
        return _repository.findById(id).map(task -> {
            if (!task.getUser().getId().equals(requester.getId()))
                throw new AccessDeniedException("Access denied");

            if (updatedTask.getTitle() != null) task.setTitle(updatedTask.getTitle());
            if (updatedTask.getDescription() != null) task.setDescription(updatedTask.getDescription());
            if (updatedTask.getCompleted() != null) task.setCompleted(updatedTask.getCompleted());
            return _repository.save(task);
        }).orElseThrow(EntityNotFoundException::new);
    }

    public void deleteTask(long id, AppUser requester) {
        _repository.findById(id).map(task -> {
            if (!task.getUser().getId().equals(requester.getId()))
                throw new AccessDeniedException("Access denied");
            return task;
        }).orElseThrow(EntityNotFoundException::new);
        _repository.deleteById(id);
    }
}
