package com.example.cameldynamicpipeline.handler;

import org.apache.camel.Processor;
import org.apache.camel.model.RouteDefinition;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.example.cameldynamicpipeline.config.ApplicationContextProvider;
import com.example.cameldynamicpipeline.entity.IntegrationStep;

@Component("processor")
public class ProcessorStepHandler implements StepHandler {

    @Override
    public RouteDefinition apply(RouteDefinition route, IntegrationStep step) {
        String bean = new JSONObject(step.getConfig()).getString("bean");
        Processor processor = ApplicationContextProvider.getBean(bean, Processor.class);
        return route.process(processor);
    }
}

