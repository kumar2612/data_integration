package com.example.cameldynamicpipeline.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "step_executions")
public class StepExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "flow_execution_id")
    private Long flowExecutionId;
    
    @Column(name = "step_id")
    private Integer stepId;
    
    @Column(name = "step_type")
    private String stepType;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "records_processed")
    private Integer recordsProcessed;
    
    @Column(name = "error", columnDefinition = "TEXT")
    private String error;
}