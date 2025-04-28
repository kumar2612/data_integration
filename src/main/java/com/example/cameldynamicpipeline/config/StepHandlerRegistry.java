package com.example.cameldynamicpipeline.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.cameldynamicpipeline.handler.StepHandler;

@Component
public class StepHandlerRegistry {

    private final Map<String, StepHandler> handlers = new HashMap<>();

    @Autowired
    public StepHandlerRegistry(List<StepHandler> handlerList) {
        for (StepHandler handler : handlerList) {
            String key = handler.getClass().getAnnotation(Component.class).value();
            handlers.put(key, handler);
        }
    }

    public StepHandler getHandler(String stepType) {
        StepHandler handler = handlers.get(stepType);
        if (handler == null) {
            throw new RuntimeException("No handler for step type: " + stepType);
        }
        return handler;
    }
}

