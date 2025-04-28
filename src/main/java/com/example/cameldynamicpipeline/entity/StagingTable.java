package com.example.cameldynamicpipeline.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "staging_table")
public class StagingTable {
    @Id
    private Integer id;
    private String name;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}