package com.ak.task_manger_api.tasks.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Boolean completed;
    private Long userId;
}
