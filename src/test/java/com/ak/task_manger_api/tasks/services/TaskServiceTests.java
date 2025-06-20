package com.ak.task_manger_api.tasks.services;

import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.repositories.TaskRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTests {
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldReturnAllTask() {
        List<Task> mockTasks = List.of(
                new Task(1L, "Test 1", "Desc 1", false),
                new Task(2L, "Test 2", "Desc 2", true),
                new Task(3L, "Test 3", "Desc 3", false)
        );
        when(taskRepository.findAll()).thenReturn(mockTasks);

        List<Task> result = taskService.getAllOwnedTasks();

        assertEquals(mockTasks.size(), result.size());
        verify(taskRepository).findAll();
    }

    @Nested
    class GetTaskByIdTests {
        @Test
        void shouldReturnTaskWhenExists() {
            Task task = new Task(1L,"title","desc",false);
            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

            Optional<Task> result = taskService.getTaskById(1);

            assertTrue(result.isPresent());
            assertEquals(task, result.get());
            verify(taskRepository).findById(1L);
        }
        @Test
        void shouldReturnEmptyWhenNotExists() {
            when(taskRepository.findById(100L)).thenReturn(Optional.empty());

            Optional<Task> result = taskService.getTaskById(100L);

            assertTrue(result.isEmpty());
            verify(taskRepository).findById(100L);
        }
    }

    @Test
    void shouldCreateAndReturnTask () {
        Task task = Task.builder().title("Task 5").description("Desc 5").completed(false).build();
        Task createdTask = new Task(1L, task.getTitle(), task.getDescription(), task.isCompleted());

        when(taskRepository.save(task)).thenReturn(createdTask);

        Task result = taskService.createTask(task);

        assertNotNull(result.getId());
        assertEquals(result.getId(), createdTask.getId());

        verify(taskRepository).save(task);
    }
    @Nested
    class UpdateTaskTests {
        @Test
        void shouldUpdateTaskWhenExists(){
            Task task = new Task(44L, "title", "desc", false);
            Task updatedTask = new Task(null, "updated title", "desc", true);

            when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

            Task result = taskService.updateTask(task.getId(), updatedTask);

            assertEquals(result.getId(), task.getId());
            assertEquals(result.getTitle(), updatedTask.getTitle());
            assertEquals(result.getDescription(), updatedTask.getDescription());
            assertEquals(result.isCompleted(), updatedTask.isCompleted());

            verify(taskRepository).findById(task.getId());
            verify(taskRepository).save(task);
        }
        @Test
        void shouldThrowExceptionWhenNotExists(){
            when(taskRepository.findById(0L)).thenReturn(Optional.empty());

            Task task = new Task(null, "title", "desc", false);

            assertThrows(RuntimeException.class, () -> taskService.updateTask(0, task));

            verify(taskRepository).findById(0L);
            verify(taskRepository, never()).save(any());
        }
    }

    @Test
    void shouldDeleteTask () {
        doNothing().when(taskRepository).deleteById(0L);

        taskService.deleteTask(0L);

        verify(taskRepository).deleteById(0L);
    }
}
