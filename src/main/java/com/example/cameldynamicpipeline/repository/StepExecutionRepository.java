package com.example.cameldynamicpipeline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.cameldynamicpipeline.entity.StepExecution;

public interface StepExecutionRepository extends JpaRepository<StepExecution, Long> {
}