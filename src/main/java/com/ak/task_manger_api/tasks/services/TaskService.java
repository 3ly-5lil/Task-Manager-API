package com.ak.task_manger_api.tasks.services;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.tasks.DTO.TaskRequest;
import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
    @Autowired
    private final TaskRepository _repository;

    public Page<Task> getAllOwnedTasks(AppUser requester, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return _repository.findByUserAndDeletedFalse(requester, pageable);
    }

    public Task getTaskById(long id, AppUser requester) {

        return _repository.findByIdAndUserAndDeletedFalse(id, requester).orElseThrow(EntityNotFoundException::new);
    }

    public Task createTask(TaskRequest task, AppUser requester) {
        Task created = Task.fromDTO(task);
        created.setUser(requester);
        return _repository.save(created);
    }

    public Task updateTask(long id, TaskRequest updatedTask, AppUser requester) throws RuntimeException {
        return _repository.findById(id).map(task -> {
            if (!task.getUser().getId().equals(requester.getId()))
                throw new AccessDeniedException("Access denied");

            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setCompleted(updatedTask.getCompleted());

            return _repository.save(task);
        }).orElseThrow(EntityNotFoundException::new);
    }

    public void deleteTask(long id, AppUser requester) {
        Task task = _repository.findByIdAndUserAndDeletedFalse(id, requester).orElseThrow(EntityNotFoundException::new);
        task.setDeleted(true);
        _repository.save(task);
    }

    public Task restoreTask(long id, AppUser user) {
        Task task = _repository.findByIdAndUserAndDeletedTrue(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Deleted task not found"));

        task.setDeleted(false);
        return _repository.save(task);
    }
}
