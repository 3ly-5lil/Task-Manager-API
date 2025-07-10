package com.ak.task_manger_api.auth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String role; // Example: "USER"
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @ColumnDefault("NOW()")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    @ColumnDefault("NOW()")
    private LocalDateTime updatedAt;
}
