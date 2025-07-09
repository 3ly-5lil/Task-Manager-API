package com.ak.task_manger_api.tasks.repositories;

import com.ak.task_manger_api.tasks.models.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(long userId);

    Page<Task> findByUserId(long userId, Pageable pageable);
}
