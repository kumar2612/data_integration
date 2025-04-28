package com.example.cameldynamicpipeline.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "integration_flows")
public class IntegrationFlow {
    @Id
    private Integer id;
    private String name;
    @Column(name = "schedule_cron")
    private String scheduleCron;
    private Boolean active;

    @OneToMany(mappedBy = "flow", cascade = CascadeType.ALL)
    private List<IntegrationStep> steps;
}