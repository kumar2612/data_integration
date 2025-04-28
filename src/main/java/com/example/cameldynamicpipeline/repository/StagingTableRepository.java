package com.example.cameldynamicpipeline.repository;

import com.example.cameldynamicpipeline.entity.StagingTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagingTableRepository extends JpaRepository<StagingTable, Integer> {
}