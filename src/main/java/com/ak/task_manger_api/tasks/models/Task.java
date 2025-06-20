package com.ak.task_manger_api.tasks.models;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.tasks.DTO.TaskRequest;
import com.ak.task_manger_api.tasks.DTO.TaskResponse;
import jakarta.persistence.*;
import lombok.*;

@Data @Entity @NoArgsConstructor @AllArgsConstructor @Builder
public class Task {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String description;
    Boolean completed;
    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    AppUser user;

    public TaskResponse toDTO() {
        return new TaskResponse(id, title, description, completed, user.getId());
    }

    static public Task fromDTO(TaskRequest taskRequest) {
        return Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .completed(taskRequest.getCompleted())
                .build();
    }
}
