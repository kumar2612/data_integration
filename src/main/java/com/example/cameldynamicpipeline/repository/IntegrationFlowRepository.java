package com.example.cameldynamicpipeline.repository;

import com.example.cameldynamicpipeline.entity.IntegrationFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface IntegrationFlowRepository extends JpaRepository<IntegrationFlow, Integer> {
    @Query("SELECT f FROM IntegrationFlow f WHERE f.active = true")
    List<IntegrationFlow> findByActiveTrue();
}
