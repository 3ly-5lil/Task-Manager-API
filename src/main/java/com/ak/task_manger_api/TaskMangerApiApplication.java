package com.ak.task_manger_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TaskMangerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskMangerApiApplication.class, args);
    }

}
