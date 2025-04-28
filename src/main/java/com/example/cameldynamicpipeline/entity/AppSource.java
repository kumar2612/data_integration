package com.example.cameldynamicpipeline.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "app_source")
public class AppSource {
    @Id
    private Integer id;
    private String name;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}