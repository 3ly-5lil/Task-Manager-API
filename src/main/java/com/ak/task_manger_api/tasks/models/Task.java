package com.ak.task_manger_api.tasks.models;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.tasks.DTO.TaskRequest;
import com.ak.task_manger_api.tasks.DTO.TaskResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String description;
    Boolean completed;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    AppUser user;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @ColumnDefault("NOW()")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    @ColumnDefault("NOW()")
    private LocalDateTime updatedAt;
    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean deleted = false;

    static public Task fromDTO(TaskRequest taskRequest) {
        return Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .completed(taskRequest.getCompleted())
                .build();
    }

    public TaskResponse toDTO() {
        return new TaskResponse(id, title, description, completed, createdAt, updatedAt);
    }
}
