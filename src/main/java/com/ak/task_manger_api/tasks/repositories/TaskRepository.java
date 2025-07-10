package com.ak.task_manger_api.tasks.repositories;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.tasks.models.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByUserAndDeletedFalse(AppUser userId, Pageable pageable);

    Optional<Task> findByIdAndUserAndDeletedFalse(long id, AppUser user);

    Optional<Task> findByIdAndUserAndDeletedTrue(long id, AppUser user);
}
