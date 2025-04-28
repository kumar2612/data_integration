package com.example.cameldynamicpipeline.repository;

import com.example.cameldynamicpipeline.entity.AppSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppSourceRepository extends JpaRepository<AppSource, Integer> {
}