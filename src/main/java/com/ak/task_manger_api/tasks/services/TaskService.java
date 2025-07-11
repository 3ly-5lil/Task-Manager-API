package com.ak.task_manger_api.tasks.services;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.tasks.DTO.TaskRequest;
import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    @Autowired
    private final TaskRepository _repository;

    public Page<Task> getAllOwnedTasks(AppUser user, int page, int size) {
        log.info("Getting tasks for user '{}', page={}, size={}", user.getUsername(), page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Task> tasks = _repository.findByUserAndDeletedFalse(user, pageable);

        log.info("Tasks retrieved successfully");

        return tasks;
    }

    public Task getTaskById(long id, AppUser user) {
        log.info("Getting task for user '{}', with id={}", user.getUsername(), id);

        Task task = _repository.findByIdAndUserAndDeletedFalse(id, user).orElseThrow(EntityNotFoundException::new);

        log.info("Task retrieved successfully id={}, title={}", id, task.getTitle());

        return task;
    }

    public Task createTask(TaskRequest taskRequest, AppUser user) {
        log.info("User '{}' is creating a new task with title '{}'", user.getUsername(), taskRequest.getTitle());

        Task created = Task.fromDTO(taskRequest);
        created.setUser(user);

        Task saved = _repository.save(created);

        log.info("Task with id '{}' created successfully for user '{}'", saved.getId(), user.getUsername());
        return saved;
    }

    public Task updateTask(long id, TaskRequest taskRequest, AppUser user) throws RuntimeException {
        log.info("User '{}' is updating task '{}'", user.getUsername(), id);

        Task task = _repository.findByIdAndUserAndDeletedFalse(id, user).orElseThrow(EntityNotFoundException::new);
        log.debug("Existing task data before update: {}", task);

        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setCompleted(taskRequest.getCompleted());

        Task updated = _repository.save(task);

        log.info("Task '{}' updated successfully for user '{}'", updated.getId(), user.getUsername());

        return updated;
    }

    public void deleteTask(long id, AppUser user) {
        log.info("User '{}' is attempting to delete task '{}'", user.getUsername(), id);

        Task task = _repository.findByIdAndUserAndDeletedFalse(id, user).orElseThrow(EntityNotFoundException::new);

        task.setDeleted(true);
        _repository.save(task);

        log.info("Task '{}' marked as deleted by user '{}'", id, user.getUsername());
    }

    public Task restoreTask(long id, AppUser user) {
        log.info("User '{}' is restoring deleted task '{}'", user.getUsername(), id);

        Task task = _repository.findByIdAndUserAndDeletedTrue(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Deleted task not found"));

        task.setDeleted(false);
        Task restored = _repository.save(task);

        log.info("Task '{}' restored successfully by user '{}'", restored.getId(), user.getUsername());

        return restored;
    }
}
