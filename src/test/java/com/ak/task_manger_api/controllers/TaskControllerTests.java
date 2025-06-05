package com.ak.task_manger_api.controllers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest @AutoConfigureMockMvc
public class TaskControllerTests {

    @Test
    void shouldReturnAllTasks() {}
    @Nested
    class GetTaskByIdTest{
        @Test
        void shouldReturnTaskIfExists(){

        }

        @Test
        void shouldReturnNotFoundIfNotExists(){}
    }
    @Test
    void shouldSaveTask(){}
    @Nested
    class UpdateTaskTest{
        @Test
        void shouldUpdateTaskIfExists(){
        }
        @Test
        void shouldThrowExceptionIfNotExists(){}
    }
    @Test
    void shouldDeleteTask(){}
}
