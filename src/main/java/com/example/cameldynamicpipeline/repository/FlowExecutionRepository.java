package com.example.cameldynamicpipeline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.cameldynamicpipeline.entity.FlowExecution;

public interface FlowExecutionRepository extends JpaRepository<FlowExecution, Long> {
}