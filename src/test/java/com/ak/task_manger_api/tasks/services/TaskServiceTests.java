package com.ak.task_manger_api.tasks.services;

import com.ak.task_manger_api.auth.models.AppUser;
import com.ak.task_manger_api.tasks.DTO.TaskRequest;
import com.ak.task_manger_api.tasks.models.Task;
import com.ak.task_manger_api.tasks.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

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

    private AppUser mockUser;

    @BeforeEach
    void setUp() {
        mockUser = AppUser.builder().id(1L).username("User_01").password("Password").role("USER").build();
    }

    @Test
    void shouldReturnAllTask() {
        List<Task> mockTasks = List.of(
                new Task(1L, "Test 1", "Desc 1", false, mockUser),
                new Task(2L, "Test 2", "Desc 2", true, mockUser),
                new Task(3L, "Test 3", "Desc 3", false, mockUser),
                new Task(3L, "Test 3", "Desc 3", false, new AppUser(0L, null, null, null))
        );
        when(taskRepository.findByUserId(mockUser.getId())).thenReturn(mockTasks.subList(0, 3));

        List<Task> result = taskService.getAllOwnedTasks(mockUser);

        assertEquals(mockTasks.size() - 1, result.size());
        verify(taskRepository).findByUserId(mockUser.getId());
    }

    @Test
    void shouldCreateAndReturnTask() {
        TaskRequest taskRequest = new TaskRequest("Task 5", "Desc 5", false);
        Task task = Task.fromDTO(taskRequest);
        task.setUser(mockUser);

        Task createdTask = new Task(1L, task.getTitle(), task.getDescription(), task.getCompleted(), mockUser);

        when(taskRepository.save(task)).thenReturn(createdTask);

        Task result = taskService.createTask(taskRequest, mockUser);

        assertNotNull(result.getId());
        assertEquals(result.getId(), createdTask.getId());

        verify(taskRepository).save(task);
    }

    @Nested
    class DeleteTaskTests {
        @Test
        void shouldDeleteTaskWhenExistAndOwned() {
            Task mockedTask = new Task(1L, "Task", "Desc", false, mockUser);

            when(taskRepository.findById(mockedTask.getId())).thenReturn(Optional.of(mockedTask));
            doNothing().when(taskRepository).deleteById(mockedTask.getId());

            taskService.deleteTask(mockedTask.getId(), mockUser);

            verify(taskRepository).deleteById(mockedTask.getId());
        }

        @Test
        void shouldThrowAccessDeniedExceptionWhenTaskNotOwned() {
            Task mockedTask = new Task(1L, "Task", "Desc", false, AppUser.builder().id(0L).build());

            when(taskRepository.findById(mockedTask.getId())).thenReturn(Optional.of(mockedTask));

            assertThrows(AccessDeniedException.class, () -> taskService.deleteTask(mockedTask.getId(), mockUser));

            verify(taskRepository, never()).deleteById(mockedTask.getId());
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWhenTaskNotExists() {
            Task mockedTask = new Task(1000L, "Task", "Desc", false, null);

            when(taskRepository.findById(mockedTask.getId())).thenThrow(EntityNotFoundException.class);

            assertThrows(EntityNotFoundException.class, () -> taskService.deleteTask(mockedTask.getId(), mockUser));

            verify(taskRepository, never()).deleteById(mockedTask.getId());
        }
    }

    @Nested
    class GetTaskByIdTests {
        @Test
        void shouldReturnTaskWhenExists() {
            Task task = new Task(1L, "title", "desc", false, mockUser);
            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

            Task result = taskService.getTaskById(1L, mockUser);

            assertEquals(task, result);
            verify(taskRepository).findById(1L);
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWhenNotExists() {
            when(taskRepository.findById(100L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(100L, eq(mockUser)));

            verify(taskRepository).findById(100L);
        }

        @Test
        void shouldThrowAccessDeniedExceptionWhenNotOwned() {
            var actualOwner = AppUser.builder().id(100L).build();
            var mockTask = new Task(100L, "T", "D", false, actualOwner);

            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));

            assertThrows(AccessDeniedException.class, () -> taskService.getTaskById(100L, mockUser));

            verify(taskRepository).findById(100L);
        }
    }

    @Nested
    class UpdateTaskTests {
        @Test
        void shouldUpdateTaskWhenExists() {
            Task task = new Task(44L, "title", "desc", false, mockUser);
            TaskRequest taskRequest = new TaskRequest("updated title", "desc", true);

            Task updatedTask = Task.fromDTO(taskRequest);
            updatedTask.setUser(mockUser);
            updatedTask.setId(task.getId());

            when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

            Task result = taskService.updateTask(task.getId(), taskRequest, mockUser);

            assertEquals(result.getId(), updatedTask.getId());
            assertEquals(result.getTitle(), updatedTask.getTitle());
            assertEquals(result.getDescription(), updatedTask.getDescription());
            assertEquals(result.getCompleted(), updatedTask.getCompleted());

            verify(taskRepository).findById(task.getId());
            verify(taskRepository).save(task);
        }

        @Test
        void shouldThrowEntityNotFoundExceptionWhenNotExists() {
            when(taskRepository.findById(1L)).thenReturn(Optional.empty());

            TaskRequest taskRequest = new TaskRequest("title", "desc", false);

            assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(1L, taskRequest, mockUser));

            verify(taskRepository).findById(1L);
            verify(taskRepository, never()).save(any());
        }

        @Test
        void shouldThrowAccessDeniedExceptionWhenNotOwned() {
            var actualOwner = AppUser.builder().id(100L).build();
            var mockTask = new Task(100L, "T", "D", false, actualOwner);

            when(taskRepository.findById(100L)).thenReturn(Optional.of(mockTask));

            assertThrows(AccessDeniedException.class, () -> taskService.updateTask(100L, any(), mockUser));

            verify(taskRepository).findById(100L);
            verify(taskRepository, never()).save(any());
        }
    }
}
