package com.example.cameldynamicpipeline.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "flow_executions")
public class FlowExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "flow_id")
    private Integer flowId;
    
    @Column(name = "flow_name")
    private String flowName;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "error", columnDefinition = "TEXT")
    private String error;
}