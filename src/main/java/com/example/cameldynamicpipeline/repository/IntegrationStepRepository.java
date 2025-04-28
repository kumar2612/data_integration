package com.example.cameldynamicpipeline.repository;

import com.example.cameldynamicpipeline.entity.IntegrationStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IntegrationStepRepository extends JpaRepository<IntegrationStep, Integer> {
    List<IntegrationStep> findByFlowIdOrderByStepOrder(Integer flowId);
}