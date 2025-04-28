package com.example.cameldynamicpipeline.handler;

import org.apache.camel.model.RouteDefinition;

import com.example.cameldynamicpipeline.entity.IntegrationStep;

public interface StepHandler {
    RouteDefinition apply(RouteDefinition route, IntegrationStep step);
}

