package com.example.cameldynamicpipeline.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "integration_steps")
public class IntegrationStep {
    @Id
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "flow_id")
    private IntegrationFlow flow;
    
    @Column(name = "step_order")
    private Integer stepOrder;
    
    @Column(name = "step_type")
    private String stepType;
    
    @Column(columnDefinition = "json")
    private String config;
}